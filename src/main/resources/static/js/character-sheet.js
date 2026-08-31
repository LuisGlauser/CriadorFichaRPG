
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

        status.textContent =
            "Curando...";

        status.dataset.state =
            "saving";

        try {

            const response =
                await fetch(
                    `/api/characters/${encodeURIComponent(id)}/heal`,
                    {
                        method: "POST",
                        headers: {
                            "Accept":
                                "application/json"
                        }
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

            status.textContent =
                "Curado ✓";

            status.dataset.state =
                "saved";

        } catch (error) {

            console.error(error);

            status.textContent =
                "Erro ao curar";

            status.dataset.state =
                "error";
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
