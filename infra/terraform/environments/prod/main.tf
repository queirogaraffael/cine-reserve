###############################################################################
# cine-reserve — Ambiente PROD
# Sobe apenas: VPC + Subnets privadas + RDS PostgreSQL (Multi-AZ)
###############################################################################

module "networking" {
  source = "../../modules/networking"

  project_name         = var.project_name
  environment          = "prod"
  vpc_cidr             = var.vpc_cidr
  availability_zones   = var.availability_zones
  private_subnet_cidrs = ["10.1.10.0/24", "10.1.11.0/24"]
}

module "database" {
  source = "../../modules/database"

  project_name      = var.project_name
  environment       = "prod"
  subnet_ids        = module.networking.private_subnet_ids
  security_group_id = module.networking.database_security_group_id

  instance_class = "db.t3.small"
  db_name        = "cine_reserve"
  db_username    = var.db_username
  db_password    = var.db_password
  multi_az       = true # Alta disponibilidade em prod
}
