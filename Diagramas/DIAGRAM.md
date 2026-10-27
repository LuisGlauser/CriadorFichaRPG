classDiagram
direction LR
class CharacterComponent {
+getName() String
+getAttributes() Attributes
+getLevel() int
+getCharacterClass() CharacterClass
+getSpecies() Species
+getBackground() Backgrounds
+getMaxHp() int
+getCurrentHp() int
+getTemporaryHp() int
+getFeatures() List~Features~
+getActiveEffects() String
+takeDamage(int) void
+heal(int) void
}

    class CharacterSheetDecorator {
	    #wrapped CharacterComponent
	    +CharacterSheetDecorator(CharacterComponent)
    }

    class ItemBoostDecorator {
	    -itemName String
	    -attribute String
	    -fixedValue int
	    +getAttributes() Attributes
	    +getActiveEffects() String
    }

    class TemporaryHpDecorator {
	    -temporaryHp int
	    +getTemporaryHp() int
	    +takeDamage(int) void
	    +getActiveEffects() String
    }

    class CharacterSheet {
	    -name String
	    -level int
	    -maxHp int
	    -currentHp int
	    -temporaryHp int
	    -attributes Attributes
	    -characterClass CharacterClass
	    -species Species
	    -background Backgrounds
	    -features List~Features~
	    +takeDamage(int) void
	    +heal(int) void
	    +getActiveEffects() String
    }

    class Attributes {
	    -strength int
	    -dexterity int
	    -constitution int
	    -intelligence int
	    -wisdom int
	    -charisma int
	    +getModifier(int) int
	    +getConModifier() int
    }

    class Features {
	    -name String
	    -level Integer
	    -levels List~Integer~
	    -effects List~Effect~
	    -activation String
	    -usage Usage
	    +clone() Features
    }

    class CharacterClass {
	    -id String
	    -name String
	    -hitDie int
	    -savingThrowProficiencies List~String~
	    -weaponProficiencies List~String~
	    -armorTraining List~String~
	    -features Map
	    +clone() CharacterClass
    }

    class Species {
	    -id String
	    -name String
	    -creatureType String
	    -size Size
	    -speed Speed
	    -traits List~Features~
	    +clone() Species
    }

    class Backgrounds {
	    -id String
	    -name String
	    -feat String
	    -skillProficiencies List~String~
	    -startingEquipment StartingEquipment
	    +clone() Backgrounds
    }

    class CharacterBuilder {
	    -name String
	    -level int
	    -characterClass CharacterClass
	    -background Backgrounds
	    -species Species
	    -attributes Attributes
	    +setName(String) CharacterBuilder
	    +setLevel(int) CharacterBuilder
	    +setClass(CharacterClass) CharacterBuilder
	    +setSpecies(Species) CharacterBuilder
	    +setBackground(Backgrounds) CharacterBuilder
	    +setAttributes(Attributes) CharacterBuilder
	    +build() CharacterSheet
	    -mergeFeatures() List~Features~
	    -calculateHp() int
    }

    class CharacterSheetFacade {
	    -characterService CharacterService
	    -classRegistry ClassRegistry
	    -speciesRegistry SpeciesRegistry
	    -backgroundRegistry BackgroundRegistry
	    +buildAndSave(CharacterCreationDTO) CharacterSheet
	    +getFeaturesUpToLevel(String, int) Map
	    +getLastCharacter() CharacterSheet
    }

    class CharacterService {
	    -lastCharacter CharacterSheet
	    +create(CharacterCreationDTO) CharacterSheet
	    +getLastCharacter() CharacterSheet
    }

    class ClassRegistry {
	    -Map~String,CharacterClass~ classes
	    +findById(String id) CharacterClass
	    +findAll() List~CharacterClass~
    }

    class SpeciesRegistry {
	    -Map~String,Species~ species
	    +findById(String id) Species
	    +findAll() List~Species~
    }

    class BackgroundRegistry {
	    -Map~String,Backgrounds~ backgrounds
	    +findById(String id) Backgrounds
	    +findAll() List~Backgrounds~
    }

    class EffectsState {
	    -glovesEquipped boolean
	    -temporaryHp int
	    +setGlovesEquipped(boolean) void
	    +setTemporaryHp(int) void
	    +removeAllEffects() void
    }

    class CharacterViewController {
	    -characterService CharacterService
	    +viewCharacter(HttpSession) String
	    +applyEffects(String, int, HttpSession) String
	    -getOrCreateState(HttpSession) EffectsState
    }

	<<interface>> CharacterComponent
	<<abstract>> CharacterSheetDecorator

    CharacterComponent <|.. CharacterSheet
    CharacterComponent <|.. CharacterSheetDecorator
    CharacterSheetDecorator <|-- ItemBoostDecorator
    CharacterSheetDecorator <|-- TemporaryHpDecorator
    CharacterSheetDecorator o-- CharacterComponent
    CharacterSheet *-- Attributes
    CharacterSheet *-- CharacterClass
    CharacterSheet *-- Species
    CharacterSheet *-- Backgrounds
    CharacterSheet *-- Features
    CharacterClass *-- Features
    Species *-- Features
    CharacterBuilder ..> CharacterSheet : cria
    CharacterSheetFacade --> CharacterService
    CharacterSheetFacade --> ClassRegistry
    CharacterSheetFacade --> SpeciesRegistry
    CharacterSheetFacade --> BackgroundRegistry
    CharacterService --> CharacterBuilder
    CharacterService --> ClassRegistry
    CharacterService --> SpeciesRegistry
    CharacterService --> BackgroundRegistry
    CharacterViewController --> CharacterService : busca lastCharacter
    CharacterViewController --> EffectsState : lê e salva na sessão
    CharacterViewController ..> ItemBoostDecorator : instancia se glovesEquipped
    CharacterViewController ..> TemporaryHpDecorator : instancia se temporaryHp > 0
    CharacterViewController ..> CharacterComponent : monta cadeia de decorators