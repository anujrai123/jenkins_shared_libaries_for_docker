def call(String playbook, String targetList, String run_action = "default", String inventoryPath = "inventory/hosts") {
    def targets = targetList.split(',').collect { it.trim() }
    echo "Running Ansible Playbook: ${playbook}"
    echo "Run action: ${run_action}"
    echo "Targets: ${targets.join(',')}"

    // Generate inventory file dynamically based on targets
    def inventoryContent = "[targets]\n"
    targets.each { target ->
        inventoryContent += "${target}\n"
    }
    writeFile file: inventoryPath, text: inventoryContent

    withCredentials([sshUserPrivateKey(credentialsId: 'automation', keyFileVariable: 'SSH_KEY')]) {
        sh """
        docker exec ansible \
            ansible-playbook /ansible_playbooks/playbooks/${playbook} \
            -i /ansible_playbooks/playbooks/${inventoryPath} \
            --extra-vars "run_action=${run_action}" \
            --limit "${targets.join(',')}" \
            --private-key ${SSH_KEY}
        """
    }
}
