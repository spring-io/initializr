{
  "files": [
    {
      "aql": {
        "items.find": {
          "$and": [
            {
              "@build.name": "${buildName}",
              "@build.number": "${buildNumber}"
            }
          ]
        }
      },
      "target": "nexus/"
    }
  ]
}
