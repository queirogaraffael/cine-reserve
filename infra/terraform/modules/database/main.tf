locals {
  name_prefix = "${var.project_name}-${var.environment}"

  common_tags = merge(var.tags, {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  })
}

# ─── Subnet Group ─────────────────────────────────────────────────────────────

resource "aws_db_subnet_group" "main" {
  name        = "${local.name_prefix}-db-subnet-group"
  description = "Subnet group for RDS PostgreSQL"
  subnet_ids  = var.subnet_ids

  tags = merge(local.common_tags, { Name = "${local.name_prefix}-db-subnet-group" })
}

# ─── RDS PostgreSQL ───────────────────────────────────────────────────────────

resource "aws_db_instance" "main" {
  identifier     = "${local.name_prefix}-postgres"
  engine         = "postgres"
  engine_version = "15.7"
  instance_class = var.instance_class

  allocated_storage   = 20
  storage_type        = "gp3"
  storage_encrypted   = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.security_group_id]
  publicly_accessible    = false

  multi_az            = var.multi_az
  skip_final_snapshot = true

  tags = merge(local.common_tags, { Name = "${local.name_prefix}-postgres" })
}
