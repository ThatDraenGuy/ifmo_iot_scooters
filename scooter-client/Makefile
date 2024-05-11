CODE = scooter_client
PROTOS = $(shell find ../proto -name *.proto)

env:
	poetry install
	poetry shell

run:
	poetry run python -m scooter_client

format:
	poetry run python -m isort $(CODE)
	poetry run python -m black $(CODE)

lint:
	poetry run python -m pylint $(CODE)

pb: clean
	mkdir proto
	poetry run python -m grpc_tools.protoc -I=.. --pyi_out=./ --python_out=./ --grpc_python_out=./ $(PROTOS)

clean:
	rm -rf proto

.phony: pb format lint env