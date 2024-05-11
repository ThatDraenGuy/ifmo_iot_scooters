REGISTRY=cr.yandex/crpvlbeg7ia4vj0j1o82

build-push-api:
	docker compose build
	docker tag ifmo_iot_scooters-scooter-server $(REGISTRY)/scooter-server

build-push-fluent-bit:
	docker compose build
	docker tag ifmo_iot_scooters-fluent-bit $(REGISTRY)/fluent-bit

build-push-fake-client:
	docker compose build

