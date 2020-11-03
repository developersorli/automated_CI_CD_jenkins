def project_name = "cts-webserver"
def git_url = "https://github.com/sorli2se/devops.git"
def ci_job_name = "ci-job-cts-webserver"

freeStyleJob(project_name) {

    logRotator(-1, 30)

    properties{
        copyArtifactPermissionProperty {
            projectNames("ci_job_${project_name}")
        }
        githubProjectUrl(git_url)

    }

    triggers {
        githubPush()
    }

    scm {
        git {
            remote {
                url(git_url)
                name('${JOB_NAME}')
            }
            branch('main')
        }
    }

    steps {

        shell('''
cd service
echo "Release ${BUILD_NUMBER}" >> index.html
docker build -t ${JOB_NAME}:${BUILD_NUMBER} .
        ''')

        shell('''
echo "service:
  name: ${JOB_NAME}
  repo_url : $GIT_URL
  docker_image: ${JOB_NAME}
  revision: ${BUILD_NUMBER}" > metadata.txt
        ''')
    }

    publishers {
        archiveArtifacts{
            pattern('metadata.txt')
            onlyIfSuccessful()
            fingerprint()
        }
        postBuildTask {
            trigger(ci_job_name) {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp('SERVICE', '$JOB_NAME')
                }
            }
        }
    }
}


