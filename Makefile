.PHONY: dev up down logs

dev: ## Lance backend + frontend en mode dev
	docker compose -f docker-compose.dev.yml up --build

up: ## Lance sans rebuild
	docker compose -f docker-compose.dev.yml up

down: ## Stoppe tout
	docker compose -f docker-compose.dev.yml down

logs: ## Affiche les logs en live
	docker compose -f docker-compose.dev.yml logs -f
