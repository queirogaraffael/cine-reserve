output "broker_id" {
  description = "ID of the Amazon MQ broker"
  value       = aws_mq_broker.main.id
}

output "broker_arn" {
  description = "ARN of the Amazon MQ broker"
  value       = aws_mq_broker.main.arn
}

output "amqp_endpoint" {
  description = "Primary AMQPS endpoint for the RabbitMQ broker"
  value       = aws_mq_broker.main.instances[0].endpoints[0]
}

output "console_url" {
  description = "RabbitMQ management console URL"
  value       = aws_mq_broker.main.instances[0].console_url
}
