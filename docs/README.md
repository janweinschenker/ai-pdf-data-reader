# Ai PDF and Image Data Reader

This is a tool designed to read and analyze documents, extracting key information and summarizing content for quick
understanding.

## How to start and run the application

* create a file `.env` in the root directory of the project with the following content:

```dotenv
OPENAI_API_KEY=add_your_key_here
OPENAI_ORG_ID=org_12345
OPENAI_PROJECT_ID=proj_whatever
```

Replace the placeholders with your actual OpenAI API key, organization ID, and project ID.

* build the project with `./mvnw clean package`
* run the application with `./mvnw clean spring-boot:run`

Optionally, you can also configure the model and reasoning effort by adding the following lines to your `.env` file:
```dotenv
OPENAI_MODEL=gpt-5-mini

# Applies to gpt-5 only: currently supported values are minimal, low, medium, and high
OPENAI_MODEL_REASONING_EFFORT=minimal
```

Install tesseract and leptonica if you want to use OCR for image-based PDFs.

```bash
$ brew install tesseract
$ brew install leptonica
```


## What happens under the hood

The file [sample1.pdf](src/main/resources/sample1.pdf) is read and processed by the application. 
The application uses Spring AI to interact with OpenAI's GPT-4 model to analyze the content of the PDF and generate a summary.

## Further Reading

* [Spring AI Docu](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html)
* [Spring AI Docu on addtional document readers (doc, docs, ppt, html, ...)](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/document/class-use/DocumentReader.html)