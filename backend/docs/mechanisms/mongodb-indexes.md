# MongoDB indexes

How every declared index is created under its name, and an index an earlier release named is replaced.

A document declares its indexes where it declares its fields, with `@Indexed` and `@CompoundIndex`, and names each
one: `idx_<collection>_<field>…`, and `uk_<collection>_<field>…` for a unique one, the fields in `snake_case`, so an
error or a query plan that names an index says which collection and fields it covers.

| Collection       | Index                                      | Keys                               | Was named          |
|------------------|--------------------------------------------|------------------------------------|--------------------|
| `artists`        | `idx_artists_stage_id`                     | `stageId`                          | `stageId`          |
| `categories`     | `idx_categories_parent_id`                 | `parentId`                         | `parentId`         |
| `orders`         | `idx_orders_customer_id_created_at`        | `customerId`, `createdAt` (newest) | `customer_newest`  |
| `products`       | `idx_products_category_id`                 | `categoryId`                       | `categoryId`       |
| `wishlist_items` | `idx_wishlist_items_product_id`            | `productId`                        | `productId`        |
| `wishlist_items` | `uk_wishlist_items_customer_id_product_id` | `customerId`, `productId` (unique) | `customer_product` |

## Created at start-up, not by the mapping context

Spring Data's `auto-index-creation` is off. It creates the indexes while the mapping context starts, before any of the
application's code runs, and MongoDB refuses to create an index whose keys an index of another name already covers, so
renaming an index would stop the application from starting on a database an earlier release had indexed.

The back-end has no migration tool, and a step that converges needs none. `config/MongoIndexes` runs once every bean
exists, like the seeding of the shop settings and the translations, before the server takes a request. For each
`@Document` and each index it declares, it drops an index on the same keys under another name, then creates the
declared one; creating an index that already exists is a no-op. Every start brings the database to what the code
declares, so nothing is recorded, and a failure to create an index, such as a unique index over documents that break
it, stops the start-up rather than leaving the collection without it.

## The window of a rename

MongoDB allows one index on the same keys, so the old one is dropped before its replacement is created. For that
moment the collection has no index on those keys, and while the wishlist's unique index is renamed nothing refuses a
second entry for the same product; one written then makes the creation fail, and the start-up with it, instead of
passing unnoticed.
