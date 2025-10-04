def call(String playbook, String targetList, String action = "default") {
    def targets = targetList.split(',').collect { it.trim() }.join('\n')

    echo "Running Ansible Playbook: ${playbook}"
    echo "Action: ${action}"
    echo "Targets: ${targets}"

    withCredentials([sshUserPrivateKey(credentialsId: 'automation', keyFileVariable: 'SSH_KEY')]) {
        // Generate dynamic inventory file in workspace
        writeFile file: 'temp_inventory.ini', text: """
        [dynamic_targets]
        ${targetList.split(',').collect { it.trim() + " ansible_user=automation ansible_ssh_private_key_file=${SSH_KEY}" }.join('\n')}
        """

        // Run Ansible playbook directly inside Jenkins container
        sh """
        ansible-playbook /ansible/playbooks/${playbook} \
            -i temp_inventory.ini \
            --extra-vars "action=${action}"
        """
    }
}
