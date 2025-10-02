// vars/deployAnsible.groovy
def call(String playbook, String targetList, String action = "default", String inventory = "inventory/hosts") {
    // Split comma-separated target list
    def targets = targetList.split(',').collect { it.trim() }.join(',')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    // Ensure SSH key is mounted from Jenkins credentials
    withCredentials([sshUserPrivateKey(credentialsId: 'ansible-ssh-key', keyFileVariable: 'SSH_KEY')]) {

        // Check if the ansible container is running
        def containerRunning = sh(
            script: "docker ps -q -f name=ansible",
            returnStdout: true
        ).trim()

        if (containerRunning == "") {
            echo "Ansible container not running. Starting container..."
            sh """
            docker-compose up -d ansible
            """
        } else {
            echo "Ansible container is already running: ${containerRunning}"
        }

        // Run the playbook inside the persistent Ansible container
        sh """
        docker exec ansible \
          ansible-playbook /ansible/playbooks/${playbook} \
          -i /ansible/playbooks/${inventory} \
          --extra-vars "action=${action}" \
          --limit "${targets}"
        """
    }
}
