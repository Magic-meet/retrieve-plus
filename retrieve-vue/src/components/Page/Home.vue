<script setup>
import { onMounted, ref } from "vue";
import RagService from "@/components/service/ragService.js";

const query = ref("");
const mode = ref("hybrid");
const topK = ref(5);
const documentId = ref(null);
const loading = ref(false);
const results = ref([]);
const documents = ref([]);
const errorMessage = ref("");

const retrievalModes = [
  { title: "混合检索", value: "hybrid" },
  { title: "关键词检索", value: "keyword" },
  { title: "语义检索", value: "semantic" }
];

const search = async () => {
  if (!query.value) {
    errorMessage.value = "请输入查询内容";
    return;
  }
  errorMessage.value = "";
  loading.value = true;
  try {
    const response = await RagService.retrieval(mode.value, {
      query: query.value,
      topK: topK.value,
      documentId: documentId.value
    });
    results.value = response.results || [];
  } catch (error) {
    errorMessage.value = error.response?.data?.message || "检索失败";
  } finally {
    loading.value = false;
  }
};

const statusLabel = status => {
  const labels = {
    STORED: "已保存",
    PARSED: "已解析",
    PARSING: "解析中",
    FAILED: "失败",
    WAITING: "等待中"
  };
  return labels[status] || status;
};

const scoreLabel = score => (score ?? 0).toFixed(4);

const retrievalTypeLabel = type => {
  const labels = {
    hybrid: "混合检索",
    keyword: "关键词检索",
    semantic: "语义检索"
  };
  return labels[type] || type;
};

onMounted(async () => {
  documents.value = await RagService.listDocuments();
});
</script>

<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card class="pa-4" rounded="lg">
          <v-card-title class="d-flex align-center">
            <v-icon class="me-2">mdi-database-search</v-icon>
            RAG 检索台
          </v-card-title>
          <v-card-subtitle>
            使用标准化的关键词、语义和混合检索接口，直接验证文档召回效果。
          </v-card-subtitle>
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field
                    v-model="query"
                    label="查询内容"
                    placeholder="例如：Transformer 的注意力机制"
                    prepend-inner-icon="mdi-magnify"
                    @keyup.enter="search"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="3">
                <v-select
                    v-model="mode"
                    :items="retrievalModes"
                    label="检索模式"
                ></v-select>
              </v-col>
              <v-col cols="12" md="3">
                <v-select
                    v-model="topK"
                    :items="[3, 5, 10]"
                    label="返回数量"
                ></v-select>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12" md="9">
                <v-select
                    v-model="documentId"
                    :items="documents.filter(item => item.parseStatus === 'PARSED').map(item => ({
                      title: `${item.title || item.originalFileName} (${statusLabel(item.parseStatus)})`,
                      value: item.documentId
                    }))"
                    label="文档筛选"
                    clearable
                    hint="可选：仅在某个已解析文档内检索"
                    persistent-hint
                ></v-select>
              </v-col>
              <v-col cols="12" md="3">
                <v-btn block color="primary" size="large" :loading="loading" @click="search">
                  开始检索
                </v-btn>
              </v-col>
            </v-row>
            <v-alert v-if="errorMessage" type="error" variant="tonal" class="mt-4">
              {{ errorMessage }}
            </v-alert>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12">
        <v-card rounded="lg">
          <v-card-title>检索结果</v-card-title>
          <v-card-text>
            <v-alert v-if="!loading && results.length === 0" type="info" variant="tonal">
              暂无结果，先上传并解析文档，再试一次检索。
            </v-alert>
            <v-expansion-panels v-else variant="accordion">
              <v-expansion-panel v-for="item in results" :key="`${item.paperId}-${item.retrievalType}`">
                <v-expansion-panel-title>
                  <div class="d-flex flex-column w-100">
                    <span class="font-weight-medium">{{ item.title }}</span>
                    <span class="text-caption text-grey">
                      {{ item.authors }} | {{ item.venue || '未知来源' }} | 得分 {{ scoreLabel(item.score) }}
                    </span>
                  </div>
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-chip color="primary" size="small" label class="me-2">
                    {{ retrievalTypeLabel(item.retrievalType) }}
                  </v-chip>
                  <div class="text-body-2 mt-3">
                    {{ item.abstractContent }}
                  </div>
                  <v-divider class="my-4"></v-divider>
                  <div class="text-subtitle-2 mb-2">命中片段</div>
                  <v-card
                      v-for="chunk in item.chunks"
                      :key="chunk.chunkId"
                      class="mb-3"
                      variant="outlined"
                  >
                    <v-card-item>
                      <v-card-title class="text-body-2">
                        {{ chunk.secNum || '-' }} {{ chunk.section || '未命名章节' }}
                      </v-card-title>
                      <v-card-subtitle>{{ chunk.type || '段落' }}</v-card-subtitle>
                    </v-card-item>
                    <v-card-text>{{ chunk.text }}</v-card-text>
                  </v-card>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
