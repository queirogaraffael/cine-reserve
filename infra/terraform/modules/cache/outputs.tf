output "redis_cluster_id" {
  description = "ID of the ElastiCache Redis cluster"
  value       = aws_elasticache_cluster.main.id
}

output "redis_endpoint" {
  description = "Connection endpoint of the Redis cluster"
  value       = aws_elasticache_cluster.main.cache_nodes[0].address
}

output "redis_port" {
  description = "Port of the Redis cluster"
  value       = aws_elasticache_cluster.main.port
}
