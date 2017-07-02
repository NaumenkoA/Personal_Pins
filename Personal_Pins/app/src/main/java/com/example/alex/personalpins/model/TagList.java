package com.example.alex.personalpins.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TagList extends ArrayList <Tag> {

    @Override
    public boolean add(Tag tag) {
        final boolean result = super.add(tag);
        Collections.sort(this);
        return result;
    }
}
