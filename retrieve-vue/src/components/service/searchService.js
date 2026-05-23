import axios from "axios";


class SearchService {
    static async search (formData) {
        const res = await axios.post(
            '/document/search',
            formData
        )
        try {
                return res.data
        } catch (err) {
            return err
        }
    }

    static async detail (formData) {
        const res = await axios.post(
            '/document/deatil',
            formData
        )
        try {
            return res.data
        } catch (err) {
            return err
        }
    }

    static async recommend(formData){
        const res = await axios.post(
            '/document/recommend',
            formData
        )
        try {
            return res.data
        } catch (err) {
            return err
        }
    }
}

export default SearchService