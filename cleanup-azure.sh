#!/bin/bash

# Azure Cleanup Script

set -e

echo "========== Azure Cleanup Started =========="

RESOURCE_GROUP="library-management-rg"

echo "This will delete ALL resources in resource group: $RESOURCE_GROUP"
read -p "Are you sure? (yes/no): " confirm

if [ "$confirm" == "yes" ]; then
    echo "Deleting resource group and all resources..."
    az group delete \
      --name $RESOURCE_GROUP \
      --yes \
      --no-wait
    
    echo "Deletion initiated. Resources will be removed in the background."
    echo "To check status:"
    echo "az group show --name $RESOURCE_GROUP"
else
    echo "Cleanup cancelled."
fi

echo "========== Azure Cleanup Completed =========="