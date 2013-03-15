#!/bin/bash
parent_sha=$(git show-ref -s refs/heads/gh-pages)
doc_sha=$(git ls-tree -d HEAD website | awk '{print $3}')
doc_commit_message=$(git log -n 1 --format="%s" $doc_dir)
new_commit=$(echo $doc_commit_message | git commit-tree $doc_sha -p $parent_sha)
git update-ref refs/heads/gh-pages $new_commit
git push origin gh-pages
