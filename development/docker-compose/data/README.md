# Custom Data

1. Place custom Data json file(s) here.
    > **NOTE**: The json file should be compatible with `bootstrap-job` selected [version](../.env). For more details, refer to `bootstrap-job` document

2. Select the custom file(s) via properties under `bootstrap-job` service:
    ```shell
      bootstrap-job:
        ...
        environment:
          ...
          backbase.bootstrap.data.active-data-sets: local-backend-setup
          backbase.bootstrap.data.data-sets[0].name: local-backend-setup
          backbase.bootstrap.data.data-sets[0].legal-entity-files: file:///opt/resources/custom/legal-entity/LegalEntity.json
          backbase.bootstrap.data.data-sets[0].product-catalog-files: file:///opt/resources/custom/product-catalog/products.json
          ...
    ```
