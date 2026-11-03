<template>
  <div>
    <div class="admin-page-head">
      <div><h1>寄养管理</h1><p>查看寄养申请，为宠物分配房间并跟进照护</p></div>
      <div class="actions">
        <router-link to="/admin/rooms" class="a-btn">房间管理</router-link>
        <button class="a-btn" @click="load">刷新</button>
      </div>
    </div>

    <div class="room-overview">
      <div v-for="r in rooms" :key="r.id" class="room-card" :class="'room-' + r.status">
        <div class="room-top"><strong>{{ r.roomNumber }}</strong><span class="status-tag" :class="r.status === 0 ? 'ok' : r.status === 1 ? 'warn' : 'danger'">{{ ['空闲', '已满', '维修'][r.status] }}</span></div>
        <span class="room-type">{{ r.type }} · 容量 {{ r.capacity }}</span>
        <p>{{ r.description }}</p>
      </div>
      <div class="room-card add-room" @click="$router.push('/admin/rooms')"><span>＋</span><small>管理房间</small></div>
    </div>

    <div class="a-filter">
      <el-select v-model="statusFilter" size="small" clearable placeholder="全部状态" style="width:130px" @change="load">
        <el-option v-for="(s, i) in statuses" :key="i" :label="s" :value="i" />
      </el-select>
      <span class="spacer"></span>
      <span class="table-note">共 {{ list.length }} 份寄养申请</span>
    </div>

    <div class="a-card">
      <table class="a-table">
        <thead><tr><th>宠物</th><th>申请人</th><th>寄养时间</th><th>房间</th><th>状态</th><th>备注</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="f in filtered" :key="f.id">
            <td><div class="main-cell"><span class="pet-avatar">{{ (f.petName || '宠').slice(0, 1) }}</span><div><strong>{{ f.petName }}</strong><small>{{ f.categoryName || '品种未填' }} · {{ f.petGender }} · {{ f.petAge }} 个月</small></div></div></td>
            <td>{{ f.username || '用户 #' + f.userId }}</td>
            <td>{{ f.startDate }} <span class="to">至</span> {{ f.endDate }}</td>
            <td>{{ f.roomNumber || '待分配' }}</td>
            <td><span class="status-tag" :class="statusClass(f.status)">{{ statuses[f.status] }}</span></td>
            <td><span class="note-text">{{ f.description || '—' }}</span></td>
            <td>
              <div class="op">
                <button v-if="f.status === 0" class="a-link" @click="openAssign(f)">分配房间</button>
                <button v-else class="a-link" @click="openDetail(f)">查看</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="a-empty"><div class="icon">🏠</div><p>暂无寄养申请</p></div>
    </div>

    <el-dialog title="分配寄养房间" :visible.sync="assignVisible" width="560px">
      <div v-if="current" class="assign-head">
        <div><strong>{{ current.petName }}</strong><span>{{ current.categoryName }} · {{ current.startDate }} 至 {{ current.endDate }}</span></div>
        <span class="pill">{{ current.username }}</span>
      </div>
      <p class="assign-tip">请选择一间空闲房间。分配后，用户可在个人中心查看房间号。</p>
      <div class="room-select">
        <button v-for="r in availableRooms" :key="r.id" :class="{ active: selectedRoom === r.id }" @click="selectedRoom = r.id">
          <strong>{{ r.roomNumber }}</strong>
          <span>{{ r.type }} · 容量 {{ r.capacity }}</span>
          <small>{{ r.description }}</small>
        </button>
      </div>
      <div v-if="!availableRooms.length" class="a-empty"><p>当前没有空闲房间，请先在房间管理中调整状态。</p></div>
      <div slot="footer">
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedRoom" :loading="assigning" @click="assign">确认分配</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, putData } from '../../utils/api'
import { demoFosters, demoRooms, demoCategories } from '../../utils/demo'

export default {
  name: 'FosterManage',
  data() {
    return {
      list: demoFosters.map((f, i) => Object.assign({ username: ['夏天的风', '橘子汽水'][i] || '用户' }, f)),
      rooms: demoRooms,
      statuses: ['待处理', '已分配', '已完成', '已拒绝'],
      statusFilter: null,
      assignVisible: false,
      assigning: false,
      current: null,
      selectedRoom: null
    }
  },
  computed: {
    filtered() {
      if (this.statusFilter === null || this.statusFilter === '') return this.list
      return this.list.filter(f => f.status === this.statusFilter)
    },
    availableRooms() {
      return this.rooms.filter(r => r.status === 0)
    }
  },
  created() {
    document.title = '寄养管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    statusClass(s) { return s === 1 ? 'ok' : s === 2 ? 'info' : s === 3 ? 'danger' : 'warn' },
    async load() {
      const [f, r] = await Promise.all([
        getData('/foster/adminPage', { pageNum: 1, pageSize: 100 }, { list: this.list }),
        getData('/room/list', null, demoRooms)
      ])
      if (f && f.list && f.list.length) this.list = f.list
      if (r && r.length) this.rooms = r
    },
    openAssign(f) {
      this.current = f
      this.selectedRoom = null
      this.assignVisible = true
    },
    openDetail(f) {
      this.current = f
      this.selectedRoom = f.roomId
      this.assignVisible = true
    },
    async assign() {
      this.assigning = true
      await putData('/foster/assign/' + this.current.id, { roomId: this.selectedRoom }, { ok: true })
      const room = this.rooms.find(r => r.id === this.selectedRoom)
      this.current.roomId = this.selectedRoom
      this.current.roomNumber = room ? room.roomNumber : ''
      this.current.status = 1
      this.assigning = false
      this.assignVisible = false
      this.$message.success('房间分配成功')
    }
  }
}
</script>

<style scoped>
.room-overview { display: grid; grid-template-columns: repeat(6, 1fr); gap: 10px; margin-bottom: 16px; }
.room-card { background: var(--paper); border: 1px solid var(--line); border-radius: 12px; padding: 13px 14px; }
.room-top { display: flex; align-items: center; justify-content: space-between; }
.room-top strong { font-size: 13px; }
.room-type { font-size: 9px; color: var(--muted); }
.room-card p { font-size: 8.5px; color: var(--muted-2); margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.room-1 { background: #fdf9f3; }
.room-2 { opacity: .65; }
.add-room { display: flex; flex-direction: column; align-items: center; justify-content: center; border-style: dashed; cursor: pointer; color: var(--muted); }
.add-room span { font-size: 18px; }
.add-room small { font-size: 8.5px; }
.table-note { font-size: 9.5px; color: var(--muted); }
.pet-avatar { width: 30px; height: 30px; border-radius: 9px; background: var(--cream-2); color: var(--ink-2); display: flex; align-items: center; justify-content: center; font-size: 11px; }
.to { color: var(--muted-2); font-size: 9px; margin: 0 2px; }
.note-text { display: block; max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--muted); }
.assign-head { display: flex; justify-content: space-between; align-items: center; background: var(--cream); border-radius: 10px; padding: 12px 14px; }
.assign-head div { display: flex; flex-direction: column; }
.assign-head strong { font-size: 14px; }
.assign-head span { font-size: 9.5px; color: var(--muted); margin-top: 2px; }
.assign-tip { font-size: 10.5px; color: var(--muted); margin: 14px 0 10px; }
.room-select { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.room-select button { border: 1px solid var(--line); background: var(--paper); border-radius: 10px; padding: 12px; text-align: left; display: flex; flex-direction: column; cursor: pointer; }
.room-select button:hover { border-color: var(--line-strong); }
.room-select button.active { border-color: var(--primary); background: var(--primary-soft); }
.room-select strong { font-size: 13px; }
.room-select span { font-size: 9.5px; color: var(--muted); margin-top: 2px; }
.room-select small { font-size: 8.5px; color: var(--muted-2); margin-top: 2px; }
@media (max-width: 1200px) {
  .room-overview { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 700px) {
  .a-card { overflow-x: auto; }
  .a-table { min-width: 820px; }
  .room-overview { grid-template-columns: 1fr 1fr; }
}
</style>
