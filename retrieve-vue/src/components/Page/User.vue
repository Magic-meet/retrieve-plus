<script setup>
import { computed, ref } from "vue";
import { apiBaseUrl } from "@/config/runtimeConfig.js";

const sections = [
  {
    id: "overview",
    title: "总览",
    icon: "mdi-compass-outline",
    items: [
      {
        id: "api-overview",
        title: "RAG 接口总览",
        method: "INFO",
        path: "/api/v1/*",
        summary: "面向 RAG 系统的统一接口规范，覆盖文档摄取、文档管理与多种检索方式。",
        description: "推荐的工作流：先上传文档，等待解析完成，再通过 `keyword`、`semantic` 或 `hybrid` 检索接口进行召回。",
        highlights: [
          "统一的资源命名：documents 对应知识库文档，retrieval 对应查询能力。",
          "所有接口都适合直接接入前端、Agent 或外部服务编排流程。"
        ],
        curl: `curl ${apiBaseUrl}/api/v1/documents`,
        responseExample: `{
  "mode": "hybrid",
  "query": "transformer attention",
  "total": 3,
  "results": [
    {
      "documentId": 12,
      "paperId": 9,
      "title": "Attention Is All You Need",
      "score": 0.8333,
      "retrievalType": "hybrid",
      "chunks": []
    }
  ]
}`
      }
    ]
  },
  {
    id: "documents",
    title: "文档",
    icon: "mdi-file-document-multiple-outline",
    items: [
      {
        id: "documents-upload",
        title: "上传文档",
        method: "POST",
        path: "/api/v1/documents",
        summary: "上传 PDF 文档，系统保存源文件并触发后台解析。",
        description: "文档上传后会先写入系统记录，再进入 FastDFS 存储和解析流程。返回值里包含 `documentId`、上传状态、解析状态等字段，可用于后续查询或删除。",
        requestHeaders: [
          { name: "Content-Type", value: "multipart/form-data", required: "是", description: "文件上传表单。" }
        ],
        requestFields: [
          { name: "file", type: "File", required: "是", description: "待上传的 PDF 文件。" }
        ],
        curl: `curl -X POST ${apiBaseUrl}/api/v1/documents \\
  -F "file=@/path/to/paper.pdf"`,
        responseExample: `{
  "documentId": 12,
  "title": null,
  "originalFileName": "paper.pdf",
  "uploadStatus": "STORED",
  "parseStatus": "PARSING",
  "message": "源文件已保存，正在解析文献内容"
}`
      },
      {
        id: "documents-list",
        title: "获取文档列表",
        method: "GET",
        path: "/api/v1/documents",
        summary: "获取系统中的全部文档记录。",
        description: "返回文档元数据、解析状态、分块数量等信息，适合做知识库列表页或运营后台。",
        curl: `curl -X GET ${apiBaseUrl}/api/v1/documents`,
        responseExample: `[
  {
    "documentId": 12,
    "paperId": 9,
    "title": "Attention Is All You Need",
    "authors": "Ashish Vaswani; Noam Shazeer",
    "chunkCount": 42,
    "uploadStatus": "STORED",
    "parseStatus": "PARSED"
  }
]`
      },
      {
        id: "documents-detail",
        title: "获取文档详情",
        method: "GET",
        path: "/api/v1/documents/{documentId}",
        summary: "获取单个文档的完整信息。",
        description: "适合详情页、调试页或摄取状态追踪。文档尚未解析完成时，论文级元数据可能为空。",
        pathParams: [
          { name: "documentId", type: "Long", required: "是", description: "系统内文档记录 ID。" }
        ],
        curl: `curl -X GET ${apiBaseUrl}/api/v1/documents/12`,
        responseExample: `{
  "documentId": 12,
  "paperId": 9,
  "title": "Attention Is All You Need",
  "year": "2017",
  "venue": "NIPS",
  "abstractContent": "The dominant sequence transduction models...",
  "chunkCount": 42
}`
      },
      {
        id: "documents-chunks",
        title: "获取分块列表",
        method: "GET",
        path: "/api/v1/documents/{documentId}/chunks",
        summary: "返回文档解析后的 chunk 列表。",
        description: "这是 RAG 调试里最重要的接口之一，可直接查看文档切分结果、章节结构和 chunk 文本。",
        pathParams: [
          { name: "documentId", type: "Long", required: "是", description: "系统内文档记录 ID。" }
        ],
        curl: `curl -X GET ${apiBaseUrl}/api/v1/documents/12/chunks`,
        responseExample: `[
  {
    "chunkId": 101,
    "section": "Introduction",
    "secNum": "1",
    "type": "paragraph",
    "text": "Recurrent neural networks..."
  }
]`
      },
      {
        id: "documents-source",
        title: "下载源文件",
        method: "GET",
        path: "/api/v1/documents/{documentId}/source",
        summary: "下载文档对应的原始 PDF 文件。",
        description: "适合提供知识库源文件追溯能力，也可用于二次处理。",
        pathParams: [
          { name: "documentId", type: "Long", required: "是", description: "系统内文档记录 ID。" }
        ],
        curl: `curl -L ${apiBaseUrl}/api/v1/documents/12/source -o paper.pdf`,
        responseExample: `PDF 二进制流`
      },
      {
        id: "documents-delete",
        title: "删除文档",
        method: "DELETE",
        path: "/api/v1/documents/{documentId}",
        summary: "删除文档记录及关联结构化数据、索引和源文件。",
        description: "适合知识库管理场景。删除后，文档元数据、chunk、检索索引以及原始文件都会被清理。",
        pathParams: [
          { name: "documentId", type: "Long", required: "是", description: "系统内文档记录 ID。" }
        ],
        curl: `curl -X DELETE ${apiBaseUrl}/api/v1/documents/12`,
        responseExample: `HTTP 200 / 204`
      }
    ]
  },
  {
    id: "retrieval",
    title: "检索",
    icon: "mdi-database-search-outline",
    items: [
      {
        id: "retrieval-keyword",
        title: "关键词检索",
        method: "POST",
        path: "/api/v1/retrieval/keyword",
        summary: "基于 chunk 文本匹配的关键词检索。",
        description: "适合术语、短语、实体名等精确召回需求。系统会优先从 chunk 文本里找直接匹配，并返回命中的 chunk 片段。",
        requestHeaders: [
          { name: "Content-Type", value: "application/json", required: "是", description: "JSON 请求体。" }
        ],
        requestFields: [
          { name: "query", type: "String", required: "是", description: "检索查询文本。" },
          { name: "topK", type: "Integer", required: "否", description: "返回结果数量，默认 5。" },
          { name: "documentId", type: "Long", required: "否", description: "限制在某个文档内检索。" }
        ],
        curl: `curl -X POST ${apiBaseUrl}/api/v1/retrieval/keyword \\
  -H "Content-Type: application/json" \\
  -d '{"query":"attention mechanism","topK":5}'`,
        responseExample: `{
  "mode": "keyword",
  "query": "attention mechanism",
  "total": 2,
  "results": [
    {
      "documentId": 12,
      "paperId": 9,
      "title": "Attention Is All You Need",
      "score": 1.0,
      "chunks": [
        {
          "chunkId": 101,
          "section": "Introduction",
          "text": "Attention mechanisms have become..."
        }
      ]
    }
  ]
}`
      },
      {
        id: "retrieval-semantic",
        title: "语义检索",
        method: "POST",
        path: "/api/v1/retrieval/semantic",
        summary: "基于向量召回的语义检索。",
        description: "适合自然语言问句、概念近似表达和模糊语义检索。系统会基于文档向量做语义召回。",
        requestHeaders: [
          { name: "Content-Type", value: "application/json", required: "是", description: "JSON 请求体。" }
        ],
        requestFields: [
          { name: "query", type: "String", required: "是", description: "检索查询文本。" },
          { name: "topK", type: "Integer", required: "否", description: "返回结果数量，默认 5。" },
          { name: "documentId", type: "Long", required: "否", description: "限制在某个文档内检索。" }
        ],
        curl: `curl -X POST ${apiBaseUrl}/api/v1/retrieval/semantic \\
  -H "Content-Type: application/json" \\
  -d '{"query":"how does transformer model long-range dependencies","topK":5}'`,
        responseExample: `{
  "mode": "semantic",
  "query": "how does transformer model long-range dependencies",
  "total": 3,
  "results": [
    {
      "documentId": 12,
      "paperId": 9,
      "title": "Attention Is All You Need",
      "score": 0.8333,
      "retrievalType": "semantic"
    }
  ]
}`
      },
      {
        id: "retrieval-hybrid",
        title: "混合检索",
        method: "POST",
        path: "/api/v1/retrieval/hybrid",
        summary: "融合关键词召回与语义召回的混合检索。",
        description: "这是最接近真实 RAG 生产系统的默认模式。它会融合精确匹配和语义相似度，通常能获得更稳定的召回质量。",
        requestHeaders: [
          { name: "Content-Type", value: "application/json", required: "是", description: "JSON 请求体。" }
        ],
        requestFields: [
          { name: "query", type: "String", required: "是", description: "检索查询文本。" },
          { name: "topK", type: "Integer", required: "否", description: "返回结果数量，默认 5。" },
          { name: "documentId", type: "Long", required: "否", description: "限制在某个文档内检索。" }
        ],
        curl: `curl -X POST ${apiBaseUrl}/api/v1/retrieval/hybrid \\
  -H "Content-Type: application/json" \\
  -d '{"query":"transformer attention","topK":5,"documentId":12}'`,
        responseExample: `{
  "mode": "hybrid",
  "query": "transformer attention",
  "total": 3,
  "results": [
    {
      "documentId": 12,
      "paperId": 9,
      "title": "Attention Is All You Need",
      "score": 0.9167,
      "retrievalType": "hybrid",
      "chunks": [
        {
          "chunkId": 101,
          "section": "Introduction",
          "text": "Attention mechanisms have become..."
        }
      ]
    }
  ]
}`
      }
    ]
  }
];

const allItems = sections.flatMap(section =>
  section.items.map(item => ({
    ...item,
    sectionId: section.id,
    sectionTitle: section.title,
    sectionIcon: section.icon
  }))
);

const selectedEndpoint = ref(allItems[0]);
const snackbar = ref(false);
const snackbarMessage = ref("");

const currentSection = computed(() =>
  sections.find(section => section.id === selectedEndpoint.value.sectionId)
);

const methodColor = method => ({
  GET: "success",
  POST: "primary",
  DELETE: "error",
  INFO: "grey"
}[method] || "grey");

const methodVariant = method => method === "INFO" ? "outlined" : "flat";

const copyText = async (text, label) => {
  try {
    await navigator.clipboard.writeText(text);
    snackbarMessage.value = `${label} 已复制`;
    snackbar.value = true;
  } catch (error) {
    snackbarMessage.value = `${label} 复制失败`;
    snackbar.value = true;
  }
};
</script>

<template>
  <v-container class="api-page">
    <v-row>
      <v-col cols="12" md="4" lg="3">
        <v-card rounded="xl" class="nav-card">
          <v-card-title>接口目录</v-card-title>
          <v-card-subtitle>按功能分组浏览接口</v-card-subtitle>
          <v-divider class="mt-2"></v-divider>
          <v-list nav density="comfortable" class="nav-list px-3 py-3">
            <template v-for="section in sections" :key="section.id">
              <v-list-subheader class="mt-2 nav-subheader px-2">
                <v-icon size="16" class="me-2">{{ section.icon }}</v-icon>
                {{ section.title }}
              </v-list-subheader>
              <v-list-item
                  v-for="endpoint in section.items"
                  :key="endpoint.id"
                  :active="selectedEndpoint.id === endpoint.id"
                  rounded="lg"
                  class="nav-item px-2 py-2 my-1"
                  @click="selectedEndpoint = { ...endpoint, sectionId: section.id, sectionTitle: section.title, sectionIcon: section.icon }"
              >
                <template v-slot:prepend>
                  <v-chip
                      :color="methodColor(endpoint.method)"
                      :variant="methodVariant(endpoint.method)"
                      size="x-small"
                      label
                  >
                    {{ endpoint.method }}
                  </v-chip>
                </template>
                <v-list-item-title>{{ endpoint.title }}</v-list-item-title>
                <v-list-item-subtitle>{{ endpoint.path }}</v-list-item-subtitle>
              </v-list-item>
            </template>
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="8" lg="9">
        <v-card rounded="xl" class="detail-card">
          <v-card-text class="pa-6">
            <div class="d-flex flex-column flex-md-row align-md-center justify-space-between ga-4 mb-4">
              <div>
                <div class="text-overline text-medium-emphasis mb-2">
                  <v-icon size="16" class="me-1">{{ currentSection?.icon }}</v-icon>
                  {{ currentSection?.title }}
                </div>
                <div class="d-flex align-center flex-wrap ga-3">
                  <v-chip
                      :color="methodColor(selectedEndpoint.method)"
                      :variant="methodVariant(selectedEndpoint.method)"
                      label
                  >
                    {{ selectedEndpoint.method }}
                  </v-chip>
                  <h2 class="text-h5 font-weight-bold">{{ selectedEndpoint.title }}</h2>
                </div>
                <div class="text-body-2 text-medium-emphasis mt-2 endpoint-path">
                  {{ selectedEndpoint.path }}
                </div>
              </div>
              <v-chip color="blue-grey" variant="tonal" label>
                分组：{{ selectedEndpoint.sectionTitle }}
              </v-chip>
            </div>

            <div class="detail-stack">
              <v-sheet class="doc-block" rounded="lg">
                <div class="text-subtitle-1 font-weight-medium mb-2">接口说明</div>
                <p class="text-body-1 mb-3">{{ selectedEndpoint.summary }}</p>
                <p class="text-body-2 text-medium-emphasis mb-0">{{ selectedEndpoint.description }}</p>
              </v-sheet>

              <v-sheet v-if="selectedEndpoint.highlights?.length" class="doc-block" rounded="lg">
                <div class="text-subtitle-1 font-weight-medium mb-3">设计要点</div>
                <v-list density="compact" class="bg-transparent px-0">
                  <v-list-item
                      v-for="highlight in selectedEndpoint.highlights"
                      :key="highlight"
                      class="px-0"
                  >
                    <template v-slot:prepend>
                      <v-icon color="primary" size="18">mdi-check-circle-outline</v-icon>
                    </template>
                    <v-list-item-title class="text-body-2">{{ highlight }}</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-sheet>

              <v-sheet v-if="selectedEndpoint.pathParams?.length" class="doc-block" rounded="lg">
                <div class="text-subtitle-1 font-weight-medium mb-3">路径参数</div>
                <v-table density="compact">
                  <thead>
                  <tr>
                    <th>名称</th>
                    <th>类型</th>
                    <th>必填</th>
                    <th>说明</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="row in selectedEndpoint.pathParams" :key="row.name">
                    <td>{{ row.name }}</td>
                    <td>{{ row.type }}</td>
                    <td>{{ row.required }}</td>
                    <td>{{ row.description }}</td>
                  </tr>
                  </tbody>
                </v-table>
              </v-sheet>

              <v-sheet v-if="selectedEndpoint.requestHeaders?.length" class="doc-block" rounded="lg">
                <div class="text-subtitle-1 font-weight-medium mb-3">请求头</div>
                <v-table density="compact">
                  <thead>
                  <tr>
                    <th>名称</th>
                    <th>值</th>
                    <th>必填</th>
                    <th>说明</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="row in selectedEndpoint.requestHeaders" :key="row.name">
                    <td>{{ row.name }}</td>
                    <td>{{ row.value }}</td>
                    <td>{{ row.required }}</td>
                    <td>{{ row.description }}</td>
                  </tr>
                  </tbody>
                </v-table>
              </v-sheet>

              <v-sheet v-if="selectedEndpoint.requestFields?.length" class="doc-block" rounded="lg">
                <div class="text-subtitle-1 font-weight-medium mb-3">请求体 / 字段</div>
                <v-table density="compact">
                  <thead>
                  <tr>
                    <th>名称</th>
                    <th>类型</th>
                    <th>必填</th>
                    <th>说明</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="row in selectedEndpoint.requestFields" :key="row.name">
                    <td>{{ row.name }}</td>
                    <td>{{ row.type }}</td>
                    <td>{{ row.required }}</td>
                    <td>{{ row.description }}</td>
                  </tr>
                  </tbody>
                </v-table>
              </v-sheet>

              <v-sheet class="doc-block code-block" rounded="lg">
                <div class="d-flex align-center justify-space-between mb-3">
                  <div class="text-subtitle-1 font-weight-medium">curl 示例</div>
                  <div class="d-flex ga-2">
                    <v-chip size="small" variant="tonal" label>可直接运行</v-chip>
                    <v-btn
                        size="small"
                        variant="tonal"
                        prepend-icon="mdi-content-copy"
                        @click="copyText(selectedEndpoint.curl, 'curl 命令')"
                    >
                      复制
                    </v-btn>
                  </div>
                </div>
                <pre class="api-code">{{ selectedEndpoint.curl }}</pre>
              </v-sheet>

              <v-sheet class="doc-block code-block" rounded="lg">
                <div class="d-flex align-center justify-space-between mb-3">
                  <div class="text-subtitle-1 font-weight-medium">响应示例</div>
                  <div class="d-flex ga-2">
                    <v-chip size="small" variant="tonal" label>参考</v-chip>
                    <v-btn
                        size="small"
                        variant="tonal"
                        prepend-icon="mdi-content-copy"
                        @click="copyText(selectedEndpoint.responseExample, 'JSON 示例')"
                    >
                      复制
                    </v-btn>
                  </div>
                </div>
                <pre class="api-code">{{ selectedEndpoint.responseExample }}</pre>
              </v-sheet>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar" timeout="1800">
      {{ snackbarMessage }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.api-page {
  padding-top: 12px;
}

.nav-card,
.detail-card {
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.nav-list {
  padding-left: 14px;
  padding-right: 14px;
}

.nav-subheader {
  letter-spacing: 0.06em;
}

.nav-item :deep(.v-list-item__prepend) {
  margin-inline-end: 14px;
  width: 72px;
  min-width: 72px;
  justify-content: flex-start;
}

.nav-item :deep(.v-chip) {
  width: 64px;
  justify-content: center;
}

.nav-item :deep(.v-list-item-title) {
  white-space: normal;
  line-height: 1.35;
}

.nav-item :deep(.v-list-item-subtitle) {
  white-space: normal;
  line-height: 1.3;
  margin-top: 2px;
}

.detail-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.doc-block {
  padding: 20px;
  background: #fbfcfe;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.code-block {
  background: #f3f6fb;
}

.endpoint-path {
  font-family: monospace;
}

.api-code {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: monospace;
  font-size: 13px;
  line-height: 1.65;
}
</style>
