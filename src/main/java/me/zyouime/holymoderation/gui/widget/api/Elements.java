package me.zyouime.holymoderation.gui.widget.api;

import java.util.ArrayList;
import java.util.List;

public final class Elements {

    public static <E> List<E> expandedFirst(List<E> elements) {
        return reorder(elements, true);
    }

    public static <E> List<E> expandedLast(List<E> elements) {
        return reorder(elements, false);
    }

    private static <E> List<E> reorder(List<E> elements, boolean expandedFirst) {
        int expandedCount = 0;
        for (E element : elements) {
            if (isExpanded(element)) {
                expandedCount++;
            }
        }
        if (expandedCount == 0 || expandedCount == elements.size()) {
            return elements;
        }
        List<E> ordered = new ArrayList<>(elements.size());
        for (E element : elements) {
            if (isExpanded(element) == expandedFirst) {
                ordered.add(element);
            }
        }
        for (E element : elements) {
            if (isExpanded(element) != expandedFirst) {
                ordered.add(element);
            }
        }
        return ordered;
    }

    private static boolean isExpanded(Object element) {
        return element instanceof Expandable expandable && expandable.isExpanded();
    }

    public static void collapseAll(List<?> elements) {
        for (Object element : elements) {
            if (element instanceof Expandable expandable) {
                expandable.collapse();
            }
        }
    }
}
