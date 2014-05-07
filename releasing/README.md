# Releasing DbFit

1. Make sure that all issues and pull requests on master since the last release:
    - are assigned to a milestone
    - are labelled with one of `enhancement`, `bugfix`, etc
    - all have meaningful titles (since they'll appear in the release notes)

2. Pick a release number, according to the [SemVer conventions](http://semver.org/), eg `3.2.1`.

3. Make sure than the milestone name matches the release number.

4. Bump (and push) the release number in:
    - the Gradle build (`./build.gradle`)
    - the website docs (`website/_config.yml`)

5. Fetch the build artefact from S3:

        releasing$ bundle exec ./fetch_last_successful_artefact

6. Smoke test the package by:
    - unpacking the archive
    - launching DbFit
    - running some acceptance tests

7. Once you're happy everything works, create and push a git tag with the release:

        git tag -a v<release number> -m 'Release <release number>'
        git push origin --tags

8. Generate a draft of the release notes:

        releasing$ bundle exec ./generate_release_notes <release number>

9. [Create a GitHub release](https://github.com/dbfit/dbfit/releases/new) using the GitHub tag and the release notes. I try to always summarise the contents of the release in the first few sentences.

10. Update the website by promoting the `master` docs to `gh-pages`:

        $ bin/publish-to-gh-pages.sh

11. Verify that the [DbFit website](http://dbfit.github.io/dbfit/) built correctly and that the download link to the new release is correct.

12. Announce the release on the [DbFit Google groups](https://groups.google.com/forum/#!topic/dbfit). I always pin the announcement as the top topic, and unpin the announcement about the previous release.

13. Optionally, tweet about the release :)
