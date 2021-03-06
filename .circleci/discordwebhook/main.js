const axios = require("axios").default

const branch = process.argv[2]
const version = process.argv[3]
const build = process.argv[4]
const compareUrl = process.argv[5]

const downloadUrl = "https://polluxclient.com/download?devBuild=" + build

axios
  .get(compareUrl)
  .then(res => {
    let success = true
    let description = ""

    description += "**Branch:** " + branch
    description += "\n**Status:** " + (success ? "success" : "failure")

    let changes = "\n\n**Changes:**"
    let hasChanges = false
    for (let i in res.data.commits) {
      let commit = res.data.commits[i]

      changes += "\n- [`" + commit.sha.substring(0, 7) + "`](https://github.com/PolluxDevelopment/pollux-client/commit/" + commit.sha + ") *" + commit.commit.message + "*"
      hasChanges = true
    }
    if (hasChanges) description += changes

    if (success) {
      description += "\n\n**Download:** [pollux-client-" + version + "-" + build + "](" + downloadUrl + ")"
    }

    axios.post(process.env.discord_webhook, {
      username: "Dev Builds",
      avatar_url: "https://polluxclient.com/icon.png",
      embeds: [
        {
          title: "pollux client v" + version + " build #" + build,
          description: description,
          url: "https://polluxclient.com",
          color: success ? 3066993 : 15158332,
          thumbnail: {
            url: "https://polluxclient.com/icon.png"
          }
        }
      ]
    })
  })