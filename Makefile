REGISTRY=cr.yandex/crpvlbeg7ia4vj0j1o82

tasks = $(filter-out $@,$(MAKECMDGOALS))
internalize = $(foreach wrd, $(1),$(wrd)-component)
action = COMPONENT="$@" $(MAKE) $(call internalize, $(call tasks))

push-component:
	docker compose build $(COMPONENT)
	docker tag ifmo_iot_scooters-$(COMPONENT) $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)
	docker push $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)

%-component:
	@echo
	@echo Unknown command $@!
	@echo
	@exit 1

scooter-server:
	$(action)

scooter-fake-client:
	$(action)

fluent-bit:
	$(action)

prometheus:
	$(action)

.PHONY: scooter-server fluent-bit prometheus scooter-fake-client push