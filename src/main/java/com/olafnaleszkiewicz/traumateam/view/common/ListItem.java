package com.olafnaleszkiewicz.traumateam.view.common;

public record ListItem(Long id, String label) {
    @Override
    public String toString() {
        return label;
    }
}
