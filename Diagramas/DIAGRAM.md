classDiagram

    %% ── Model: Character ──
    class CharacterSheet {
        -String name
        -int level
        -int maxHp
        -int currentHp
        -Attributes attributes
        -CharacterClass characterClass
        -Species species
        -Backgrounds background
        -List~Features~ features
        +takeDamage(int dmg) void
        +heal(int value) void
    }

    class Attributes {
        -int strength
        -int dexterity
        -int constitution
        -int intelligence
        -int wisdom
        -int charisma
        +getModifier(int score) int
    }

    class Features {
        -String name
        -Integer level
        -List~Integer~ levels
        -Map~Integer,Integer~ levelScaling
        -List~Effect~ effects
        -String activation
        -String trigger
        -Usage usage
        -String resourceInteraction
        +clone() Features
    }

    %% ── Model: Content ──
    class CharacterClass {
        -String name
        -int hitDie
        -List~String~ weaponProficiencies
        -List~String~ armorTraining
        -List~Features~ features
    }

    class Species {
        -String name
        -Size size
        -Speed speed
        -List~Features~ traits
    }

    class Backgrounds {
        -String name
        -String feat
        -List~SkillProficiency~ skillProficiencies
        -List~StartingEquipment~ startingEquipment
    }

    class Effect {
        -String type
        -String target
        -int value
        -String condition
    }

    class Usage {
        -String type
        -int times
        -String recharge
    }

    class Size {
        -String category
        -SizeOption option
    }

    class SizeOption {
        -String description
    }

    class Speed {
        -int walk
    }

    class SkillProficiency {
        -String skill
    }

    class StartingEquipment {
        -String item
        -int quantity
    }

    class EquipmentOption {
        -List~String~ options
    }

    %% ── DTOs ──
    class CharacterCreationDTO {
        -String name
        -int level
        -String classId
        -String speciesId
        -String backgroundId
    }

    class CharacterFormDTO {
        -String name
        -int level
    }

    %% ── Builder ──
    class CharacterBuilder {
        -CharacterCreationDTO dto
        -CharacterClass characterClass
        -Species species
        -Backgrounds background
        -Attributes attributes
        +withClass(CharacterClass) CharacterBuilder
        +withSpecies(Species) CharacterBuilder
        +withBackground(Backgrounds) CharacterBuilder
        +withAttributes(Attributes) CharacterBuilder
        +build() CharacterSheet
    }

    %% ── Service ──
    class CharacterService {
        -CharacterRegistry characterRegistry
        -ClassRegistry classRegistry
        -SpeciesRegistry speciesRegistry
        -BackgroundRegistry backgroundRegistry
        +buildCharacter(CharacterCreationDTO) CharacterSheet
        +saveCharacter(CharacterSheet) void
        +getCharacter(String id) CharacterSheet
    }

    %% ── Registries ──
    class CharacterRegistry {
        -Map~String,CharacterSheet~ characters
        +save(CharacterSheet) void
        +findById(String id) CharacterSheet
        +findAll() List~CharacterSheet~
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

    class SkillRegistry {
        -List~String~ skills
        +findAll() List~String~
    }

    %% ── Controllers ──
    class HomeController {
        +home() String
    }

    class CharacterWizardController {
        -CharacterService characterService
        -ClassRegistry classRegistry
        -SpeciesRegistry speciesRegistry
        -BackgroundRegistry backgroundRegistry
        +getStep1(Model) String
        +postStep1(CharacterFormDTO, Session) String
        +getStep2(Model, Session) String
        +postStep2(CharacterCreationDTO, Session) String
        +getStep3(Model, Session) String
        +postStep3(CharacterCreationDTO, Session) String
        +getStep4(Model, Session) String
        +getStep6(Model, Session) String
        +postStep6(Map, Session) String
        +finish(Session) String
    }

    class CharacterViewController {
        -CharacterService characterService
        +viewCharacter(String id, Model) String
    }

    class CharacterController {
        -CharacterService characterService
        +listCharacters() List~CharacterSheet~
        +getCharacter(String id) CharacterSheet
    }

    %% ── Prototype ──
    class Prototype~T~ {
        <<interface>>
        +clone() T
    }

    %% ── Relationships ──
    CharacterSheet "1" --> "1" Attributes
    CharacterSheet "1" --> "1" CharacterClass
    CharacterSheet "1" --> "0..1" Species
    CharacterSheet "1" --> "0..1" Backgrounds
    CharacterSheet "1" --> "*" Features

    CharacterClass "1" --> "*" Features
    Species "1" --> "*" Features : traits
    Species "1" --> "1" Size
    Species "1" --> "1" Speed
    Size "1" --> "0..1" SizeOption

    Backgrounds "1" --> "*" SkillProficiency
    Backgrounds "1" --> "*" StartingEquipment

    Features "1" --> "*" Effect
    Features "1" --> "0..1" Usage
    Features ..|> Prototype~Features~

    CharacterBuilder --> CharacterSheet : build
    CharacterBuilder ..> CharacterCreationDTO
    CharacterBuilder ..> Attributes

    CharacterService --> CharacterBuilder
    CharacterService --> CharacterRegistry
    CharacterService --> ClassRegistry
    CharacterService --> SpeciesRegistry
    CharacterService --> BackgroundRegistry

    CharacterWizardController --> CharacterService
    CharacterWizardController --> ClassRegistry
    CharacterWizardController --> SpeciesRegistry
    CharacterWizardController --> BackgroundRegistry
    CharacterWizardController ..> CharacterCreationDTO

    CharacterViewController --> CharacterService
    CharacterController --> CharacterService

    ClassRegistry --> CharacterClass : "carrega JSON"
    SpeciesRegistry --> Species : "carrega JSON"
    BackgroundRegistry --> Backgrounds : "carrega JSON"
    CharacterRegistry --> CharacterSheet : "armazena"