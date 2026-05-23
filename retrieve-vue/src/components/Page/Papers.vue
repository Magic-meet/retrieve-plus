<template>
  <v-container class="documents-page">
    <v-row>
      <v-col cols="12">
        <v-card class="section-card" rounded="xl">
          <v-card-title class="section-title d-flex align-center">
            <v-icon size="30" class="me-2 section-icon">mdi-file-document-plus-outline</v-icon>
            文档入库
          </v-card-title>
          <v-card-subtitle class="section-subtitle">
            上传 PDF 文档到系统，等待解析并进入 RAG 检索索引。
          </v-card-subtitle>
          <v-card-text>
            <div class="ingestion-panel">
              <div class="ingestion-main">
                <v-file-input
                    v-model="selectedFiles"
                    multiple
                    accept=".pdf"
                    label="选择 PDF 文件"
                    prepend-icon="mdi-file-pdf-box"
                    variant="outlined"
                    bg-color="white"
                    hide-details="auto"
                    @change="fileSelected"
                >
                  <template v-slot:selection="{ fileNames }">
                    <v-chip
                        v-for="fileName in fileNames"
                        :key="fileName"
                        class="me-2 mb-1"
                        color="primary"
                        size="small"
                        label
                    >
                      {{ fileName }}
                    </v-chip>
                  </template>
                </v-file-input>
                <div class="ingestion-meta">
                  <v-chip size="small" variant="tonal" color="blue-grey">
                    队列 {{ uploadQueue.length }}
                  </v-chip>
                  <v-chip size="small" variant="tonal" color="green">
                    已解析 {{ parsedCount }}
                  </v-chip>
                  <span class="text-caption text-medium-emphasis">
                    支持批量上传 PDF，上传成功后会自动进入解析流程。
                  </span>
                </div>
              </div>

              <v-sheet class="ingestion-side" rounded="lg">
                <div class="text-overline side-label">操作</div>
                <v-btn
                    block
                    color="primary"
                    size="default"
                    :loading="uploading"
                    :disabled="uploadQueue.length === 0"
                    @click="uploadFiles"
                >
                  上传
                </v-btn>
                <div class="text-caption text-medium-emphasis side-note">
                  当前选择 {{ uploadQueue.length }} 个文件
                </div>
              </v-sheet>
            </div>

            <v-data-table
                v-if="uploadQueue.length > 0"
                class="mt-6 upload-table"
                :headers="uploadHeaders"
                :items="uploadQueue"
                density="comfortable"
            >
              <template v-slot:item.name="{ item }">
                <div class="font-weight-medium">{{ tableItem(item).name }}</div>
              </template>
              <template v-slot:item.progress="{ item }">
                <div class="progress-cell">
                  <v-progress-linear :model-value="tableItem(item).progress" color="primary" height="10" rounded></v-progress-linear>
                  <span class="text-caption text-medium-emphasis">{{ tableItem(item).progress }}%</span>
                </div>
              </template>
              <template v-slot:item.status="{ item }">
                <v-chip :color="statusColor(tableItem(item).status)" size="small" label>
                  {{ tableItem(item).status }}
                </v-chip>
              </template>
              <template v-slot:item.message="{ item }">
                <span class="text-body-2">{{ tableItem(item).message }}</span>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12">
        <v-card class="section-card" rounded="xl">
          <v-card-title class="section-title d-flex align-center">
            <v-icon size="30" class="me-2 section-icon">mdi-folder-search-outline</v-icon>
            文档列表
            <v-spacer></v-spacer>
            <v-btn variant="tonal" prepend-icon="mdi-refresh" :loading="loading" @click="loadDocuments">
              刷新
            </v-btn>
          </v-card-title>
          <v-card-subtitle class="section-subtitle">
            统一展示系统中的文档元数据、解析状态和管理操作。
          </v-card-subtitle>
          <v-data-table
              class="mt-4 documents-table"
              :headers="documentHeaders"
              :items="documents"
              :loading="loading"
              no-data-text="暂无文档"
              density="comfortable"
          >
            <template v-slot:item.title="{ item }">
              <div class="document-title-cell">
                <div class="font-weight-medium">{{ tableItem(item).title || tableItem(item).originalFileName }}</div>
                <div class="text-caption text-medium-emphasis">{{ tableItem(item).authors || "暂无作者信息" }}</div>
                <div class="text-caption text-medium-emphasis">{{ tableItem(item).originalFileName }}</div>
              </div>
            </template>
            <template v-slot:item.fileSize="{ item }">
              {{ formatBytes(tableItem(item).fileSize || 0) }}
            </template>
            <template v-slot:item.uploadTime="{ item }">
              {{ formatTime(tableItem(item).uploadTime) }}
            </template>
            <template v-slot:item.status="{ item }">
              <div class="status-stack">
                <v-chip :color="statusColor(tableItem(item).parseStatus)" size="small" label>
                  {{ statusLabel(tableItem(item).parseStatus) }}
                </v-chip>
                <v-chip :color="statusColor(tableItem(item).uploadStatus)" size="small" label>
                  {{ statusLabel(tableItem(item).uploadStatus) }}
                </v-chip>
              </div>
            </template>
            <template v-slot:item.chunkCount="{ item }">
              <div class="chunk-pill">
                {{ tableItem(item).chunkCount ?? "-" }}
              </div>
            </template>
            <template v-slot:item.actions="{ item }">
              <div class="action-stack">
                <v-btn
                    size="small"
                    variant="tonal"
                    prepend-icon="mdi-download"
                    :href="buildApiUrl(`/api/v1/documents/${tableItem(item).documentId}/source`)"
                >
                  源文件
                </v-btn>
                <v-btn
                    size="small"
                    variant="tonal"
                    color="error"
                    prepend-icon="mdi-delete"
                    @click="deleteDocument(tableItem(item).documentId)"
                >
                  删除
                </v-btn>
              </div>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import RagService from "@/components/service/ragService.js";
import { buildApiUrl } from "@/config/runtimeConfig.js";

const selectedFiles = ref([]);
const uploadQueue = ref([]);
const documents = ref([]);
const uploading = ref(false);
const loading = ref(false);

const uploadHeaders = [
  { title: "文件名", value: "name" },
  { title: "大小", value: "size" },
  { title: "进度", value: "progress" },
  { title: "状态", value: "status" },
  { title: "说明", value: "message" }
];

const documentHeaders = [
  { title: "文档", value: "title" },
  { title: "大小", value: "fileSize" },
  { title: "上传时间", value: "uploadTime" },
  { title: "状态", value: "status" },
  { title: "分块数", value: "chunkCount" },
  { title: "操作", value: "actions", sortable: false }
];

const parsedCount = computed(() => documents.value.filter(item => item.parseStatus === "PARSED").length);

const fileSelected = () => {
  uploadQueue.value = selectedFiles.value.map(file => ({
    file,
    name: file.name,
    size: formatBytes(file.size),
    progress: 0,
    status: "等待中",
    message: "等待上传"
  }));
};

const uploadFiles = async () => {
  uploading.value = true;
  for (const item of uploadQueue.value) {
    item.status = "上传中";
    item.message = "正在上传";
    try {
      const document = await RagService.uploadDocument(item.file, progressEvent => {
        const total = progressEvent.total || item.file.size;
        item.progress = Math.round((progressEvent.loaded / total) * 100);
      });
      item.status = "成功";
      item.message = document.message;
    } catch (error) {
      item.status = "失败";
      item.message = error.response?.data?.message || "上传失败";
    }
  }
  uploading.value = false;
  await loadDocuments();
};

const loadDocuments = async () => {
  loading.value = true;
  try {
    documents.value = await RagService.listDocuments();
  } finally {
    loading.value = false;
  }
};

const deleteDocument = async documentId => {
  if (!window.confirm("确认删除该文档及所有索引数据吗？")) {
    return;
  }
  await RagService.deleteDocument(documentId);
  await loadDocuments();
};

const tableItem = item => item.raw || item;

const statusColor = status => {
  if (["PARSED", "STORED", "成功"].includes(status)) return "success";
  if (["PARSING", "UPLOADING", "上传中", "WAITING", "等待中"].includes(status)) return "primary";
  if (["FAILED", "失败"].includes(status)) return "error";
  return "grey";
};

const statusLabel = status => {
  const labels = {
    PARSED: "已解析",
    PARSING: "解析中",
    STORED: "已保存",
    UPLOADING: "上传中",
    WAITING: "等待中",
    FAILED: "失败"
  };
  return labels[status] || status;
};

const formatTime = time => (time ? new Date(time).toLocaleString() : "-");

const formatBytes = bytes => {
  if (!bytes) return "0 字节";
  const units = ["字节", "KB", "MB", "GB"];
  const power = Math.floor(Math.log(bytes) / Math.log(1024));
  return `${(bytes / 1024 ** power).toFixed(2)} ${units[power]}`;
};

onMounted(loadDocuments);
</script>

<style scoped>
.documents-page {
  padding-top: 10px;
}

.section-card {
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 4px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05);
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.01em;
  padding-bottom: 4px;
}

.section-icon {
  color: rgb(30, 41, 59);
}

.section-subtitle {
  padding-bottom: 4px;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.62);
}

.ingestion-panel {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 220px;
  gap: 16px;
  align-items: stretch;
}

.ingestion-main {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.ingestion-side {
  padding: 16px;
  background: linear-gradient(180deg, #fbfcfe 0%, #f4f7fb 100%);
  border: 1px solid rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.side-label {
  color: rgba(15, 23, 42, 0.55);
  font-size: 11px;
  letter-spacing: 0.14em;
}

.side-note {
  line-height: 1.5;
  font-size: 12px;
}

.ingestion-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  font-size: 13px;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 180px;
}

.progress-cell .v-progress-linear {
  flex: 1;
}

.document-title-cell {
  display: flex;
  flex-direction: column;
  gap: 3px;
  max-width: 560px;
}

.document-title-cell .font-weight-medium {
  font-size: 16px;
  line-height: 1.35;
}

.status-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
}

.action-stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
}

.action-stack :deep(.v-btn) {
  min-width: 104px;
}

.chunk-pill {
  min-width: 42px;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: #f3f6fb;
  color: rgba(15, 23, 42, 0.72);
  font-size: 13px;
  font-weight: 600;
}

.documents-table :deep(th) {
  color: rgba(15, 23, 42, 0.72);
  font-size: 13px;
  font-weight: 600 !important;
}

.documents-table :deep(td) {
  vertical-align: middle;
  padding-top: 12px !important;
  padding-bottom: 12px !important;
}

.upload-table :deep(th) {
  color: rgba(15, 23, 42, 0.72);
  font-size: 13px;
  font-weight: 600 !important;
}

.upload-table :deep(td) {
  padding-top: 12px !important;
  padding-bottom: 12px !important;
}

.documents-table :deep(.v-data-table-footer),
.upload-table :deep(.v-data-table-footer) {
  padding-top: 6px;
}

.documents-table :deep(.v-btn),
.upload-table :deep(.v-btn) {
  letter-spacing: 0.08em;
}

@media (max-width: 960px) {
  .ingestion-panel {
    grid-template-columns: 1fr;
  }

  .ingestion-side {
    padding: 16px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>
