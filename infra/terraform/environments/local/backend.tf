terraform {
  backend "s3" {
    bucket         = "cine-reserve-tfstate-local"
    key            = "local/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "cine-reserve-tfstate-lock"
  }
}
