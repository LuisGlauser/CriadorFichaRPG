package com.glauser.rpg.CriadorFichaRPG.observer;

public interface CharacterSubject {
    void addObserver(CharacterObserver observer);
    void removeObserver(CharacterObserver observer);
    void notifyObservers();
}
