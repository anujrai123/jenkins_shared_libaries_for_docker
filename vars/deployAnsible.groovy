def runAnsiblePlaybook(String playbook, String hostnamesParam, String run_action = "default", String inventoryPath = "inventory/hosts") {
    // Split comma-separated hostnames and trim spaces
    def targets = hostnamesParam.split(',').collect { it.trim() }
    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${run_action}"
    echo "Targets: ${targets.join(',')}"

    // Build the inventory content dynamically
    def inventoryContent = "[targets]\n"
    targets.each { host ->
        inventoryContent += "${host}\n"
    }

    // Write the inventory file to the host directory (mounted into container)
    writeFile file: inventoryPath, text: inventoryContent

    // Execute the playbook inside the ansible container
    withCredentials([sshUserPrivateKey(credentialsId: 'automation', keyFileVariable: 'SSH_KEY')]) {
        sh """
        docker exec ansible \
            ansible-playbook /ansible/playbooks/${playbook} \
            -i /ansible/${inventoryPath} \
            --extra-vars "run_action=${run_action}" \
            --limit targets \
            --private-key ${SSH_KEY}
        """
    }
}
