
(() => {
    const root = document.querySelector("#character-sheet");
    if (!root) return;

    const id = root.dataset.characterId;
    const status = document.querySelector("#save-status");

    let timer = null;
    let controller = null;

    let lastServerHp =
        Number(document.querySelector("#field-current-hp")?.value || 0);

    const fields = {
        name: "#field-name",
        level: "#field-level",
        currentHp: "#field-current-hp",
        temporaryHp: "#field-temporary-hp",
        notes: "#field-notes",
        armorType: "#field-armor-type",
        armorBase: "#field-armor-base",
        shieldBonus: "#field-shield-bonus",
        strength: "#attr-strength",
        dexterity: "#attr-dexterity",
        constitution: "#attr-constitution",
        intelligence: "#attr-intelligence",
        wisdom: "#attr-wisdom",
        charisma: "#attr-charisma"
    };

    function numberValue(selector) {

        const el = document.querySelector(selector);

        if (!el || el.value === "") {
            return null;
        }

        return Number(el.value);
    }

    /**
     * HP:
     *
     * 40  -> HP absoluto = 40
     * +10 -> HP atual + 10
     * -10 -> HP atual - 10
     */
    function getHpValue() {

        const el =
            document.querySelector("#field-current-hp");

        if (!el) {
            return null;
        }

        const raw = el.value.trim();

        if (raw === "") {
            return null;
        }

        if (/^[+-]\d+$/.test(raw)) {

            const delta = Number(raw);

            return Math.max(
                0,
                lastServerHp + delta
            );
        }

        const absolute = Number(raw);

        if (Number.isNaN(absolute)) {
            return null;
        }

        return Math.max(0, absolute);
    }

    function buildPatch(changedField) {

        if (changedField === "currentHp") {
            return {
                currentHp: getHpValue()
            };
        }

        const selector = fields[changedField];

        if (!selector) {
            return {};
        }

        const el =
            document.querySelector(selector);

        if (!el) {
            return {};
        }

        const value =
            el.type === "number"
                ? numberValue(selector)
                : el.value;

        return {
            [changedField]: value
        };
    }

    async function save(changedField) {

        if (!id) {
            return;
        }

        if (controller) {
            controller.abort();
        }

        controller =
            new AbortController();

        status.textContent = "Salvando...";
        status.dataset.state = "saving";

        try {

            const response =
                await fetch(
                    `/api/characters/${encodeURIComponent(id)}`,
                    {
                        method: "PATCH",

                        headers: {
                            "Content-Type":
                                "application/json",

                            "Accept":
                                "application/json"
                        },

                        body: JSON.stringify(
                            buildPatch(changedField)
                        ),

                        signal: controller.signal
                    }
                );

            if (!response.ok) {
                throw new Error(
                    `HTTP ${response.status}`
                );
            }

            const data =
                await response.json();

            updateDerived(data);

            status.textContent = "Salvo ✓";
            status.dataset.state = "saved";

        } catch (error) {

            if (error.name === "AbortError") {
                return;
            }

            console.error(error);

            status.textContent =
                "Erro ao salvar";

            status.dataset.state =
                "error";
        }
    }

    function scheduleSave(field) {

        clearTimeout(timer);

        timer =
            setTimeout(
                () => save(field),
                350
            );
    }

    function updateDerived(data) {

        const maxHp =
            document.querySelector("#max-hp");

        const armorClass =
            document.querySelector("#armor-class");

        const currentHp =
            document.querySelector("#field-current-hp");

        const lifeState =
            document.querySelector("#life-state");

        if (maxHp) {
            maxHp.textContent =
                data.maxHp;
        }

        if (armorClass) {
            armorClass.textContent =
                data.armorClass;
        }

        if (currentHp) {
            currentHp.value =
                data.currentHp;

            lastServerHp =
                data.currentHp;
        }

        if (lifeState) {
            lifeState.textContent =
                data.lifeState;

            lifeState.dataset.state =
                getStateClass(data.lifeState);
        }

        const attrs = [
            "strength",
            "dexterity",
            "constitution",
            "intelligence",
            "wisdom",
            "charisma"
        ];

        attrs.forEach(attr => {

            const input =
                document.querySelector(
                    fields[attr]
                );

            const modifier =
                document.querySelector(
                    `#mod-${attr}`
                );

            if (input) {
                input.value =
                    data[attr];
            }

            if (modifier) {

                const mod =
                    Math.floor(
                        (data[attr] - 10) / 2
                    );

                modifier.textContent =
                    mod >= 0
                        ? `+${mod}`
                        : mod;
            }
        });
    }

    function getStateClass(state) {

        if (state === "Normal") {
            return "normal";
        }

        if (state === "Sangrando") {
            return "bleeding";
        }

        return "unconscious";
    }

    async function heal() {
        if (!id) {
            return;
        }

        /*
         * Impede múltiplos cliques enquanto a requisição
         * atual ainda está sendo processada.
         */
        if (healButton && healButton.disabled) {
            return;
        }

        if (healButton) {
            healButton.disabled = true;
            healButton.textContent = "♥ Curando...";
        }

        status.textContent = "Curando...";
        status.dataset.state = "saving";

        try {
            const response = await fetch(
                `/api/characters/${encodeURIComponent(id)}/heal`,
                {
                    method: "POST",
                    headers: {
                        "Accept": "application/json"
                    }
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();
            updateDerived(data);

            status.textContent = "Curado ✓";
            status.dataset.state = "saved";
        } catch (error) {
            console.error("Erro ao curar:", error);
            status.textContent = "Erro ao curar";
            status.dataset.state = "error";
        } finally {
            /*
             * Só libera o botão depois que a requisição
             * terminou, seja com sucesso ou erro.
             */
            if (healButton) {
                healButton.disabled = false;
                healButton.textContent = "♥ Curar";
            }
        }
    }

    const healButton =
        document.querySelector("#btn-heal");

    if (healButton) {
        healButton.addEventListener(
            "click",
            heal
        );
    }

    /*
     * Tomar dano (Command: TakeDamageCommand).
     *
     * O front-end só manda a quantidade; quem decide se o dano
     * desconta do HP temporário antes do HP atual é o comando,
     * no back-end.
     */
    async function applyDamage() {

        if (!id) {
            return;
        }

        const input =
            document.querySelector("#field-damage-amount");

        const amount =
            Number(input?.value || 0);

        if (!amount || amount <= 0) {
            return;
        }

        status.textContent = "Aplicando dano...";
        status.dataset.state = "saving";

        try {

            const response =
                await fetch(
                    `/api/characters/${encodeURIComponent(id)}/damage?amount=${encodeURIComponent(amount)}`,
                    {
                        method: "POST",
                        headers: {
                            "Accept": "application/json"
                        }
                    }
                );

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();

            updateDerived(data);
            updateTemporaryHp(data);

            if (input) {
                input.value = "";
            }

            status.textContent = "Dano aplicado ✓";
            status.dataset.state = "saved";

        } catch (error) {

            console.error(error);

            status.textContent = "Erro ao aplicar dano";
            status.dataset.state = "error";
        }
    }

    /*
     * Desfazer última ação de combate (Command: undo()).
     */
    async function undoLast() {

        if (!id) {
            return;
        }

        status.textContent = "Desfazendo...";
        status.dataset.state = "saving";

        try {

            const response =
                await fetch(
                    `/api/characters/${encodeURIComponent(id)}/undo`,
                    {
                        method: "POST",
                        headers: {
                            "Accept": "application/json"
                        }
                    }
                );

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();

            updateDerived(data);
            updateTemporaryHp(data);

            status.textContent = "Ação desfeita ✓";
            status.dataset.state = "saved";

        } catch (error) {

            console.error(error);

            status.textContent = "Erro ao desfazer";
            status.dataset.state = "error";
        }
    }

    function updateTemporaryHp(data) {

        const temporaryHp =
            document.querySelector("#field-temporary-hp");

        if (temporaryHp && data.temporaryHp !== undefined) {
            temporaryHp.value = data.temporaryHp;
        }
    }

    const damageButton =
        document.querySelector("#btn-apply-damage");

    if (damageButton) {
        damageButton.addEventListener(
            "click",
            applyDamage
        );
    }

    const undoButton =
        document.querySelector("#btn-undo");

    if (undoButton) {
        undoButton.addEventListener(
            "click",
            undoLast
        );
    }

    Object.entries(fields).forEach(
        ([field, selector]) => {

            const el =
                document.querySelector(selector);

            if (!el) {
                return;
            }

            /*
             * HP utiliza text para aceitar +20/-20.
             */
            const event =
                field === "currentHp"
                    ? "change"
                    : (
                        el.tagName === "TEXTAREA" ||
                        el.type === "text"
                            ? "blur"
                            : "change"
                    );

            el.addEventListener(
                event,
                () => scheduleSave(field)
            );
        }
    );
})();
