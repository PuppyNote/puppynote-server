package com.puppynoteserver.pet.userItemCategories.entity;

import com.puppynoteserver.global.BaseTimeEntity;
import com.puppynoteserver.pet.petItems.entity.enums.ItemCategory;
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
                name = "uq_user_item_categories_user_category",
                columnNames = {"user_id", "category"}
        ),
        indexes = @Index(name = "idx_user_item_categories_user_id_sort", columnList = "user_id, sort")
)
public class UserItemCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ItemCategory category;

    @Column(nullable = false)
    private int sort;

    public static UserItemCategory of(User user, ItemCategory category, int sort) {
        UserItemCategory userItemCategory = new UserItemCategory();
        userItemCategory.user = user;
        userItemCategory.category = category;
        userItemCategory.sort = sort;
        return userItemCategory;
    }
}
