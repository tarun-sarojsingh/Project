#!/bin/bash

# Azure Deployment Script for Library Management System

set -e

echo "========== Azure Deployment Started =========="

# Variables
RESOURCE_GROUP="library-management-rg"
LOCATION="eastus"
APP_SERVICE_PLAN="library-app-plan"
WEB_APP_NAME="library-management-app-$RANDOM"
ACR_NAME="libraryacr$RANDOM"
DB_SERVER_NAME="library-db-server-$RANDOM"
DB_NAME="librarydb"
DB_ADMIN_USER="libraryadmin"
DB_ADMIN_PASSWORD="P@ssw0rd123!" # Use Azure Key Vault in production

# Login to Azure
echo "Logging in to Azure..."
az login

# Create Resource Group
echo "Creating resource group..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION

# Create Azure Container Registry
echo "Creating Azure Container Registry..."
az acr create \
  --resource-group $RESOURCE_GROUP \
  --name $ACR_NAME \
  --sku Basic \
  --admin-enabled true

# Get ACR credentials
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query passwords[0].value -o tsv)

# Build and push Docker image
echo "Building and pushing Docker image..."
az acr build \
  --registry $ACR_NAME \
  --image library-management:latest \
  --file Dockerfile .

# Create PostgreSQL Server
echo "Creating PostgreSQL server..."
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --location $LOCATION \
  --admin-user $DB_ADMIN_USER \
  --admin-password $DB_ADMIN_PASSWORD \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --version 14 \
  --storage-size 32

# Create database
echo "Creating database..."
az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP \
  --server-name $DB_SERVER_NAME \
  --database-name $DB_NAME

# Configure firewall to allow Azure services
echo "Configuring firewall..."
az postgres flexible-server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --rule-name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

# Create App Service Plan
echo "Creating App Service Plan..."
az appservice plan create \
  --name $APP_SERVICE_PLAN \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --is-linux \
  --sku B1

# Create Web App
echo "Creating Web App..."
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $APP_SERVICE_PLAN \
  --name $WEB_APP_NAME \
  --deployment-container-image-name $ACR_NAME.azurecr.io/library-management:latest

# Configure Web App
echo "Configuring Web App..."
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $WEB_APP_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE=prod \
    DATABASE_URL="jdbc:postgresql://$DB_SERVER_NAME.postgres.database.azure.com:5432/$DB_NAME?sslmode=require" \
    DATABASE_USERNAME=$DB_ADMIN_USER \
    DATABASE_PASSWORD=$DB_ADMIN_PASSWORD \
    WEBSITES_PORT=8080

# Configure container registry credentials
echo "Configuring container registry..."
az webapp config container set \
  --name $WEB_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --docker-custom-image-name $ACR_NAME.azurecr.io/library-management:latest \
  --docker-registry-server-url https://$ACR_NAME.azurecr.io \
  --docker-registry-server-user $ACR_USERNAME \
  --docker-registry-server-password $ACR_PASSWORD

# Enable continuous deployment
echo "Enabling continuous deployment..."
az webapp deployment container config \
  --name $WEB_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --enable-cd true

# Restart Web App
echo "Restarting Web App..."
az webapp restart \
  --name $WEB_APP_NAME \
  --resource-group $RESOURCE_GROUP

# Get Web App URL
WEB_APP_URL=$(az webapp show \
  --name $WEB_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query defaultHostName -o tsv)

echo "========== Deployment Completed Successfully =========="
echo "Application URL: https://$WEB_APP_URL"
echo "Swagger UI: https://$WEB_APP_URL/swagger-ui.html"
echo ""
echo "Database Server: $DB_SERVER_NAME.postgres.database.azure.com"
echo "Database Name: $DB_NAME"
echo ""
echo "Resource Group: $RESOURCE_GROUP"
echo ""
echo "To view logs:"
echo "az webapp log tail --name $WEB_APP_NAME --resource-group $RESOURCE_GROUP"
echo ""
echo "To delete all resources:"
echo "az group delete --name $RESOURCE_GROUP --yes --no-wait"