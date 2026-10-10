import axios from 'axios'
import { TOKEN_KEY } from '../utils/authStorage.js'

const rawBase = import.meta.env.VITE_API_BASE_URL || ''
const cleanBase = rawBase
  ? rawBase.trim().replace(/\/api\/?$/, '').replace(/\/$/, '')
  : (import.meta.env.DEV ? '' : '')

const api = axios.create({
  baseURL: cleanBase,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

const get = async (url, config) => (await api.get(url, config)).data
const post = async (url, body, config) => (await api.post(url, body, config)).data
const put = async (url, body, config) => (await api.put(url, body, config)).data
const patch = async (url, config) => (await api.patch(url, undefined, config)).data
const remove = async (url) => (await api.delete(url)).data

export const authApi = {
  login: (credentials) => post('/api/auth/login', credentials),
  register: (details) => post('/api/auth/register', details),
  me: () => get('/api/auth/me'),
}

export const studentApi = {
  profile: () => get('/api/students/me/profile'),
  updateProfile: (profile) => put('/api/students/me/profile', profile),
  skills: () => get('/api/students/me/skills'),
  addSkill: (skillName) => post('/api/students/me/skills', { skillName }),
  removeSkill: (skillId) => remove(`/api/students/me/skills/${skillId}`),
}

export const careerApi = {
  list: () => get('/api/careers'),
  get: (id) => get(`/api/careers/${id}`),
  skills: (id) => get(`/api/careers/${id}/skills`),
  skillGap: (id) => get(`/api/careers/${id}/skill-gap`),
  recommendations: () => get('/api/recommendations'),
}

export const resourceApi = {
  list: () => get('/api/resources'),
  byCareer: (careerId) => get(`/api/careers/${careerId}/resources`),
}

export const roadmapApi = {
  list: () => get('/api/roadmaps/me'),
  generate: (careerId) => post('/api/roadmaps/generate', null, { params: { careerId } }),
  completeStep: (roadmapId, stepId, completed) => patch(
    `/api/roadmaps/me/${roadmapId}/steps/${stepId}/complete`,
    { params: { completed } },
  ),
}

export const progressApi = {
  mine: () => get('/api/progress/me'),
}

export const adminApi = {
  createCareer: (career) => post('/api/admin/careers', career),
  deleteCareer: (id) => remove(`/api/admin/careers/${id}`),
  createResource: (resource) => post('/api/admin/resources', resource),
  deleteResource: (id) => remove(`/api/admin/resources/${id}`),
}

export default api
