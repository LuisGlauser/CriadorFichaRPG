classDiagram
direction TB

class CharacterMediator {
    -CharacterService characterService
    +equipItem(CharacterSheet, EffectsState, String) CharacterComponent
    +unequipItem(CharacterSheet, EffectsState, String) CharacterComponent
    +applyTemporaryHp(CharacterSheet, EffectsState, int) CharacterComponent
    +removeAllEffects(CharacterSheet, EffectsState) CharacterComponent
    +buildCharacter(CharacterSheet, EffectsState) CharacterComponent
    -notifyCharacterChanges(CharacterSheet)
}

class CharacterSheet {
    -Attributes attributes
    -int currentHp
    -int maxHp
    -int armorClass
    +updateAttribute(String, int)
    +updateBatch(Runnable)
    +addObserver(CharacterObserver)
    +notifyObservers()
}

class EffectsState {
    -boolean glovesEquipped
    -int temporaryHp
    +setGlovesEquipped(boolean)
    +setTemporaryHp(int)
    +removeAllEffects()
}

class CharacterComponent {
    <<interface>>
    +getAttributes() Attributes
    +getTemporaryHp() int
    +getActiveEffects() String
}

class CharacterSheetDecorator {
    <<abstract>>
    #CharacterComponent wrapped
}

class ItemBoostDecorator {
    -String itemName
    -String attribute
    -int fixedValue
    +getAttributes() Attributes
    +getActiveEffects() String
}

class TemporaryHpDecorator {
    -int temporaryHp
    +getTemporaryHp() int
    +takeDamage(int)
}

class CharacterObserver {
    <<interface>>
    +update(CharacterSheet)
}

class DerivedStatsObserver {
    -DerivedStatsStrategy strategy
    +update(CharacterSheet)
}

class AutoSaveObserver {
    -CharacterPersistenceService persistenceService
    +update(CharacterSheet)
}

class CharacterService {
    +getById(String) CharacterSheet
    +save(CharacterSheet)
}

CharacterMediator --> CharacterSheet : coordena
CharacterMediator --> EffectsState : gerencia efeitos
CharacterMediator ..> CharacterComponent : monta composição
CharacterMediator ..> ItemBoostDecorator : aplica equipamento
CharacterMediator ..> TemporaryHpDecorator : aplica HP temporário
CharacterMediator ..> CharacterService : consulta/salva

CharacterSheet ..|> CharacterComponent
CharacterSheet ..> CharacterObserver : notifica
CharacterSheetDecorator ..|> CharacterComponent
CharacterSheetDecorator o--> CharacterComponent : encapsula
ItemBoostDecorator --|> CharacterSheetDecorator
TemporaryHpDecorator --|> CharacterSheetDecorator

DerivedStatsObserver ..|> CharacterObserver
AutoSaveObserver ..|> CharacterObserver