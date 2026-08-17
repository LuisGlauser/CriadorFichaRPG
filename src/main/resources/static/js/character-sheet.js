
(() => {
    const root = document.querySelector("#character-sheet");
    if (!root) return;

    const id = root.dataset.characterId;
    const status = document.querySelector("#save-status");
    let timer = null;
    let controller = null;

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
        if (!el || el.value === "") return null;
        return Number(el.value);
    }

    function buildPatch(changedField) {
        const selector = fields[changedField];
        if (!selector) return {};

        const el = document.querySelector(selector);
        if (!el) return {};

        const value = el.type === "number" ? numberValue(selector) : el.value;
        return { [changedField]: value };
    }

    async function save(changedField) {
        if (!id) return;

        if (controller) controller.abort();
        controller = new AbortController();

        status.textContent = "Salvando...";
        status.dataset.state = "saving";

        try {
            const response = await fetch(`/api/characters/${encodeURIComponent(id)}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(buildPatch(changedField)),
                signal: controller.signal
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();
            updateDerived(data);

            status.textContent = "Salvo ✓";
            status.dataset.state = "saved";
        } catch (error) {
            if (error.name === "AbortError") return;
            console.error(error);
            status.textContent = "Erro ao salvar";
            status.dataset.state = "error";
        }
    }

    function scheduleSave(field) {
        clearTimeout(timer);
        timer = setTimeout(() => save(field), 350);
    }

    function updateDerived(data) {
        document.querySelector("#max-hp").textContent = data.maxHp;
        document.querySelector("#armor-class").textContent = data.armorClass;

        const attrs = ["strength", "dexterity", "constitution",
                       "intelligence", "wisdom", "charisma"];

        attrs.forEach(attr => {
            const input = document.querySelector(fields[attr]);
            const modifier = document.querySelector(`#mod-${attr}`);

            if (input) input.value = data[attr];
            if (modifier) {
                const mod = Math.floor((data[attr] - 10) / 2);
                modifier.textContent = mod >= 0 ? `+${mod}` : mod;
            }
        });

        // Se o novo máximo ficar abaixo do HP atual, o backend corrige o HP.
        const currentHp = document.querySelector("#field-current-hp");
        if (currentHp) currentHp.value = data.currentHp;
    }

    Object.entries(fields).forEach(([field, selector]) => {
        const el = document.querySelector(selector);
        if (!el) return;

        const event = el.tagName === "TEXTAREA" || el.type === "text"
            ? "blur"
            : "change";

        el.addEventListener(event, () => scheduleSave(field));
    });
})();
