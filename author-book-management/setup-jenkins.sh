#!/bin/bash

echo "========== Jenkins Setup for Library Management System =========="

# Install required Jenkins plugins
JENKINS_PLUGINS=(
    "git"
    "maven-plugin"
    "docker-plugin"
    "docker-workflow"
    "pipeline-stage-view"
    "junit"
    "jacoco"
    "email-ext"
    "credentials-binding"
    "azure-cli"
)

echo "Installing Jenkins plugins..."
for plugin in "${JENKINS_PLUGINS[@]}"; do
    echo "Installing plugin: $plugin"
    # jenkins-cli install-plugin $plugin
done

echo "========== Jenkins Setup Complete =========="