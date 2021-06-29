def call() {
    pipeline {
    agent any
    // triggers {
    //     pollSCM '* * * * *'
    // }
    environment {
        RELEASE='0.2'
    }

    stages {
        stage('Git Checkout') {
            steps {
                //shared library call
                helloWorld()
                // Get some code from a GitHub repository
               git branch: 'main', url: 'https://github.com/Fayez74/jgsu-spring-petclinic.git'
            }
        }
        stage('Build') {
            input {
                message 'Build Now?'
                ok 'Approve'
            }
            steps {
                // Run Maven on a Unix agent.
                slackSend channel: 'general', message: 'OHHH SHIIIIT, job started'
                sh "./mvnw clean package"
            }
            

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts 'target/*.jar'
                    slackSend channel: 'general', 
                    message: "${env.RELEASE} succeded"
                } 
               
            }
        }
        stage('Final Test') {
            steps {
                echo "Test release with random value"
                script {
                    if ("${RELEASE}" < 0.5 ) {
                        writeFile file: 'test-results.txt', text:'Release needs updating'
                        sh 'cat test-results.txt'
                    }else {
                        writeFile file: 'test-results.txt', text:'Release up to date'
                        sh 'cat test-results.txt'
                    }
                    
                }
                
            }
        }
    }
}





}
