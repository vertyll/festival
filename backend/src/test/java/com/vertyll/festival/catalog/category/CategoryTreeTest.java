package com.vertyll.festival.catalog.category;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.vertyll.festival.TestTexts.text;

class CategoryTreeTest {

    private static final ObjectId CLOTHES = new ObjectId();
    private static final ObjectId T_SHIRTS = new ObjectId();

    private static final CategoryTree TREE = CategoryTree.of(
        List.of(
            new CategoryDocument(CLOTHES, text("Odzież"), null, Instant.EPOCH, Instant.EPOCH),
            new CategoryDocument(T_SHIRTS, text("Koszulki"), CLOTHES, Instant.EPOCH, Instant.EPOCH)
        )
    );

    @Test
    void pathStartsAtRootCategory() {
        assertThat(TREE.pathTo(T_SHIRTS)).extracting(Reference::name).containsExactly(text("Odzież"), text("Koszulki"));
    }

    @Test
    void categoryCannotBecomeChildOfItsDescendant() {
        assertThatThrownBy(() -> TREE.validateParent(CLOTHES, T_SHIRTS)).isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void parentMustExist() {
        assertThatThrownBy(() -> TREE.validateParent(T_SHIRTS, new ObjectId()))
            .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void corruptedTreeFailsFastInsteadOfLooping() {
        ObjectId a = new ObjectId();
        ObjectId b = new ObjectId();
        CategoryTree cyclic = CategoryTree.of(
            List.of(
                new CategoryDocument(a, text("A"), b, Instant.EPOCH, Instant.EPOCH),
                new CategoryDocument(b, text("B"), a, Instant.EPOCH, Instant.EPOCH)
            )
        );

        assertThatThrownBy(() -> cyclic.pathTo(a)).isInstanceOf(IllegalStateException.class);
    }
}
