variable "project_name" {
  description = "Project name used to prefix all resources"
  type        = string
}

variable "environment" {
  description = "Deployment environment (local, prod)"
  type        = string
}

variable "subnet_ids" {
  description = "Private subnet IDs for the Amazon MQ broker"
  type        = list(string)
}

variable "security_group_id" {
  description = "Security group ID for the Amazon MQ broker"
  type        = string
}

variable "instance_type" {
  description = "Amazon MQ broker instance type"
  type        = string
  default     = "mq.t3.micro"
}

variable "deployment_mode" {
  description = "Deployment mode: SINGLE_INSTANCE or ACTIVE_STANDBY_MULTI_AZ"
  type        = string
  default     = "SINGLE_INSTANCE"

  validation {
    condition     = contains(["SINGLE_INSTANCE", "ACTIVE_STANDBY_MULTI_AZ"], var.deployment_mode)
    error_message = "deployment_mode must be SINGLE_INSTANCE or ACTIVE_STANDBY_MULTI_AZ."
  }
}

variable "mq_username" {
  description = "Username for the RabbitMQ broker"
  type        = string
  sensitive   = true
}

variable "mq_password" {
  description = "Password for the RabbitMQ broker"
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "Additional tags to apply to all resources"
  type        = map(string)
  default     = {}
}
