def call(String playbook, String hostnamesParam, String run_action = "default", String inventoryPath = "ansible_playbooks/inventory/hosts") {
    def targets = hostnamesParam.split(',').collect { it.trim() }
    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${run_action}"
    echo "Targets: ${targets.join(',')}"

    def inventoryContent = "[targets]\n"
    targets.each { host ->
        inventoryContent += "${host}\n"
    }

    // Write inventory file to host path (mapped to container)
    writeFile file: inventoryPath, text: inventoryContent

    withCredentials([sshUserPrivateKey(credentialsId: 'automation', keyFileVariable: 'SSH_KEY')]) {
        sh """
        docker exec ansible \
            ansible-playbook /ansible/playbooks/${playbook} \
            -i /ansible/inventory/hosts \
            --extra-vars "run_action=${run_action}" \
            --limit targets \
            --private-key ${SSH_KEY}
        """
    }
}
