java -Dspring.profiles.active=docker -Dspring.config.additional-location=file:./application-docker.yml \
  -Dingest.access.control=true -Dingest.custom.service.agreements=false \
  -Dingest.balance.history=false -Dingest.transactions=true -Dtransactions.min=5 -Dtransactions.max=20 \
  -Duse.pfm.categories.for.transactions=true \
  -Dingest.approvals.for.payments=false -Dingest.approvals.for.contacts=false -Dingest.approvals.for.notifications=false -Dingest.approvals.for.batches=false \
  -Dingest.limits=false \
  -Dingest.contacts=false \
  -Dingest.notifications=false \
  -Dingest.payments=false -Dpayments.min=1 -Dpayments.max=10 -Dpayments.ootb.types=SEPA_CREDIT_TRANSFER \
  -Dingest.messages=false \
  -Didentity.feature.toggle=true -Didentity.realm=backbase -Didentity.client=bb-tooling-client \
  -Dlegal.entities.with.users.json=data/retail/legal-entities-with-users.json \
  -Dproduct.group.seed.json=data/retail/product-group-seed.json \
  -Dservice.agreements.json=data/retail/service-agreements.json \
  -jar bb-fuel.jar