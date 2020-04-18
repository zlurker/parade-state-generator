package com.example.talls.paradestategenerator;

import java.util.ArrayList;
import java.util.List;

public class EnhancedList<T> {
    private List<T> list;
    private List<Integer> unused;
    private List<Boolean> slotStatus;
    private ArrayCreator<T> creator;

    public EnhancedList(ArrayCreator<T> c) {
        list = new ArrayList<T>();
        unused = new ArrayList<Integer>();
        slotStatus = new ArrayList<Boolean>();
        creator = c;
    }

    public T[] getActiveList() {
        T[] array = creator.createNewArray(list.size() - unused.size());
        int arrayCounter = 0;

        for (int i = 0; i < list.size(); i++)
            if (slotStatus.get(i)) {
                array[arrayCounter] = list.get(i);
                arrayCounter++;
            }

        return array;
    }

    public T getGlobal(int i){
        return list.get(i);
    }

    public int returnTrueInt(int index) {
        int arrayCounter = 0;

        for (int i = 0; i < list.size(); i++)
            if (slotStatus.get(i)) {
                if (arrayCounter == index)
                    return i;
                arrayCounter++;
            }
            return -1;
    }

    public int add(T element) {
        int pos;

        if (unused.size() > 0) {
            pos = unused.get(0);
            list.set(pos, element);
            slotStatus.set(pos, true);
            unused.remove(0);
            return pos;
        }

        pos = list.size();
        list.add(element);
        slotStatus.add(true);
        return pos;
    }

    public void remove(int at) {
        unused.add(at);
        list.set(at, null);
        slotStatus.set(at, false);
    }
}