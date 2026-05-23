import axios from "axios";

class RagService {
    static async listDocuments() {
        const response = await axios.get("/api/v1/documents");
        return response.data;
    }

    static async uploadDocument(file, onUploadProgress) {
        const formData = new FormData();
        formData.append("file", file);
        const response = await axios.post("/api/v1/documents", formData, {
            headers: { "Content-Type": "multipart/form-data" },
            onUploadProgress
        });
        return response.data;
    }

    static async deleteDocument(documentId) {
        await axios.delete(`/api/v1/documents/${documentId}`);
    }

    static async getDocument(documentId) {
        const response = await axios.get(`/api/v1/documents/${documentId}`);
        return response.data;
    }

    static async getDocumentChunks(documentId) {
        const response = await axios.get(`/api/v1/documents/${documentId}/chunks`);
        return response.data;
    }

    static async retrieval(mode, payload) {
        const response = await axios.post(`/api/v1/retrieval/${mode}`, payload);
        return response.data;
    }
}

export default RagService;
