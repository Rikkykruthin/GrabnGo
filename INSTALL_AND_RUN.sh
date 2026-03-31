#!/bin/bash

echo "=========================================="
echo "Food Ordering App - Installation Script"
echo "=========================================="
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "❌ Homebrew not found. Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
else
    echo "✅ Homebrew is installed"
fi

# Install Java
if ! command -v java &> /dev/null; then
    echo "📦 Installing Java..."
    brew install openjdk@11
    sudo ln -sfn /opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk
else
    echo "✅ Java is installed"
fi

# Install Maven
if ! command -v mvn &> /dev/null; then
    echo "📦 Installing Maven..."
    brew install maven
else
    echo "✅ Maven is installed"
fi

# Install Tomcat
if ! command -v catalina &> /dev/null; then
    echo "📦 Installing Tomcat..."
    brew install tomcat
else
    echo "✅ Tomcat is installed"
fi

echo ""
echo "=========================================="
echo "Installation Complete!"
echo "=========================================="
echo ""
echo "Installed versions:"
java -version 2>&1 | head -1
mvn -version | head -1
echo "Tomcat: $(brew list --versions tomcat)"
echo "Node: $(node -v)"
echo ""
echo "Next steps:"
echo "1. Setup database: mysql -u root -p < database/schema.sql"
echo "2. Build backend: cd backend && mvn clean package"
echo "3. Run backend: See RUN_INSTRUCTIONS.md"
echo "4. Run frontend: cd frontend && npm install && npm run dev"
echo ""
