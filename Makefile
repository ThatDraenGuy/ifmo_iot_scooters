include .env
include .env.secrets

REGISTRY=cr.yandex/crpvlbeg7ia4vj0j1o82

tasks = $(filter-out $@,$(MAKECMDGOALS))
internalize = $(foreach wrd, $(1),$(wrd)-component)
action = COMPONENT="$@" $(MAKE) $(call internalize, $(call tasks))

make-builder:
	docker buildx create --name container --driver=docker-container

push-component:
	docker compose build $(COMPONENT) --builder container
	docker tag ifmo_iot_scooters-$(COMPONENT) $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)
	docker push $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)
	@echo Pushed image $(REGISTRY)/$(COMPONENT):$(shell date +"%Y%m%d.%H%M%S")-$(shell whoami)


deploy-component:
	@echo $(INSTANCE)
	@[ "$(INSTANCE)" ] || ( echo "No deployment config for $(COMPONENT)"; exit 1 )
	./envsub .env.secrets ./deploy/docker-compose.$(COMPONENT).yml > ./deploy/docker-compose.$(COMPONENT).temp.yml
	yc compute instance update-container $(INSTANCE) --docker-compose-file=./deploy/docker-compose.$(COMPONENT).temp.yml
	rm ./deploy/docker-compose.$(COMPONENT).temp.yml

%-component:
	@echo
	@echo Unknown command $@!
	@echo
	@exit 1

scooter-server:
	INSTANCE="$(YC_SCOOTER_SERVER_INSTANCE_NAME)" $(action)

scooter-fake-client:
	INSTANCE="$(YC_SCOOTER_CLIENTS_INSTANCE_NAME)" $(action)

fluent-bit:
	$(action)

prometheus-agent:
	$(action)

.PHONY: scooter-server fluent-bit prometheus scooter-fake-client push