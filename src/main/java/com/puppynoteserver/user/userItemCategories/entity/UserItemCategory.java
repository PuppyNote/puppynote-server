package com.puppynoteserver.user.userItemCategories.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
import com.puppynoteserver.user.userItemCategories.entity.enums.CategoryType;
import com.puppynoteserver.user.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_item_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_item_categories_user_type_category",
                columnNames = {"user_id", "category_type", "category"}
        ),
        indexes = @Index(name = "idx_user_item_categories_user_id_type_sort", columnList = "user_id, category_type, sort")
)
public class UserItemCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false, length = 20)
    private CategoryType categoryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ItemCategory category;

    @Column(nullable = false)
    private int sort;

    public static UserItemCategory of(User user, CategoryType categoryType, ItemCategory category, int sort) {
        UserItemCategory userItemCategory = new UserItemCategory();
        userItemCategory.user = user;
        userItemCategory.categoryType = categoryType;
        userItemCategory.category = category;
        userItemCategory.sort = sort;
        return userItemCategory;
    }
}
