# 🔍 AI Code Reviewer for GitHub Pull Requests

A Spring Boot-based GitHub bot that reviews code changes in pull requests using LLM-powered natural language analysis. The system generates meaningful, context-aware comments for each modified file, enhancing the code review process and accelerating development cycles.

---

## 🚀 Features

- ✅ Automatically analyzes and comments on pull request diffs
- 🤖 Uses OpenAI's GPT model to generate insightful suggestions
- 📄 File-level feedback with markdown-formatted responses
- 🔁 Periodic job processing for queued reviews
- 🛡️ Secure API token usage and tenant-specific configurations

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot, Hibernate
- **Scheduler**: Spring Scheduling
- **AI Integration**: OpenAI GPT (via WebClient)
- **Build Tool**: Maven
- **Database**: MySQL
- **Authentication**: GitHub App (PAT support)

---

## ✍️ Example Bot Comment

```markdown
💡 **Review for `README.md`**
- Fix spelling: "changes mide" → "changes made", "Beinnings" → "Beginnings"
- Use consistent punctuation.
- Remove edits that don't add clarity or value.
