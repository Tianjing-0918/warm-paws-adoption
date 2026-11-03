<template>
  <div>
    <div class="admin-page-head">
      <div><h1>房间管理</h1><p>维护猫舍、狗舍与隔离区房间</p></div>
      <div class="actions"><button class="a-btn primary" @click="openDialog()">＋ 添加房间</button></div>
    </div>

    <div class="room-grid">
      <div v-for="r in list" :key="r.id" class="room-card" :class="'s-' + r.status">
        <div class="r-head">
          <div><strong>{{ r.roomNumber }}</strong><span>{{ r.type }}</span></div>
          <span class="status-tag" :class="r.status === 0 ? 'ok' : r.status === 1 ? 'warn' : 'danger'">{{ ['空闲', '已满', '维修中'][r.status] }}</span>
        </div>
        <div class="capacity">
          <span>容量</span>
          <div class="beds"><i v-for="n in r.capacity" :key="n" :class="{ used: r.status === 1 && n <= r.capacity }"></i></div>
          <strong>{{ r.status === 1 ? r.capacity : 0 }}/{{ r.capacity }}</strong>
        </div>
        <p>{{ r.description || '暂无备注' }}</p>
        <div class="r-foot">
          <span>ID {{ r.id }}</span>
          <div class="op"><button class="a-link" @click="openDialog(r)">编辑</button><button class="a-link danger" @click="remove(r)">删除</button></div>
        </div>
      </div>
      <button class="room-card add-room" @click="openDialog()"><span>＋</span><strong>新增房间</strong><small>猫舍 / 狗舍 / 隔离区</small></button>
    </div>

    <el-dialog :title="form.id ? '编辑房间' : '添加房间'" :visible.sync="dialogVisible" width="460px">
      <el-form :model="form" label-position="top">
        <div class="dialog-grid">
          <el-form-item label="房间号"><el-input v-model="form.roomNumber" placeholder="例如：A103" /></el-form-item>
          <el-form-item label="房间类型">
            <el-select v-model="form.type" style="width:100%">
              <el-option label="猫舍" value="猫舍" /><el-option label="狗舍" value="狗舍" /><el-option label="隔离区" value="隔离区" />
            </el-select>
          </el-form-item>
        </div>
        <div class="dialog-grid">
          <el-form-item label="容量"><el-input v-model.number="form.capacity" type="number" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width:100%">
              <el-option label="空闲" :value="0" /><el-option label="已满" :value="1" /><el-option label="维修中" :value="2" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="备注"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存房间</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getData, postData, putData, deleteData } from '../../utils/api'
import { demoRooms } from '../../utils/demo'

export default {
  name: 'RoomManage',
  data() {
    return { list: demoRooms, dialogVisible: false, form: {} }
  },
  created() {
    document.title = '房间管理 · 暖爪管理后台'
    this.load()
  },
  methods: {
    async load() {
      const data = await getData('/room/list', null, demoRooms)
      if (data && data.length) this.list = data
    },
    openDialog(r) {
      this.form = r ? Object.assign({}, r) : { status: 0, capacity: 5, type: '猫舍' }
      this.dialogVisible = true
    },
    async save() {
      if (!this.form.roomNumber) return this.$message.warning('请填写房间号')
      if (this.form.id) await putData('/room', this.form, { ok: true })
      else await postData('/room', this.form, { ok: true })
      if (this.form.id) {
        const i = this.list.findIndex(x => x.id === this.form.id)
        if (i >= 0) this.$set(this.list, i, Object.assign({}, this.form))
      } else {
        this.list.push(Object.assign({}, this.form, { id: Date.now() }))
      }
      this.dialogVisible = false
      this.$message.success('房间已保存')
    },
    async remove(r) {
      try { await this.$confirm(`确定删除房间 ${r.roomNumber} 吗？`, '删除房间', { type: 'warning' }) } catch (e) { return }
      await deleteData('/room/' + r.id, { ok: true })
      this.list = this.list.filter(i => i.id !== r.id)
      this.$message.success('已删除')
    }
  }
}
</script>

<style scoped>
.room-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.room-card { background: var(--paper); border: 1px solid var(--line); border-radius: 14px; padding: 16px; display: flex; flex-direction: column; }
.r-head { display: flex; justify-content: space-between; align-items: flex-start; }
.r-head div { display: flex; flex-direction: column; }
.r-head strong { font-size: 18px; letter-spacing: -.02em; }
.r-head span:not(.status-tag) { font-size: 9px; color: var(--muted); }
.capacity { display: flex; align-items: center; gap: 8px; margin-top: 14px; }
.capacity > span { font-size: 8.5px; color: var(--muted); }
.beds { display: flex; gap: 3px; }
.beds i { width: 12px; height: 12px; border-radius: 3px; background: var(--cream-2); }
.beds i.used { background: var(--sage); }
.capacity strong { font-size: 9px; color: var(--muted); margin-left: auto; }
.room-card p { font-size: 9px; color: var(--muted); margin-top: 8px; flex: 1; }
.r-foot { display: flex; justify-content: space-between; align-items: center; padding-top: 10px; border-top: 1px solid var(--line); margin-top: 10px; }
.r-foot > span { font-size: 8px; color: var(--muted-2); }
.add-room { border-style: dashed; align-items: center; justify-content: center; cursor: pointer; background: transparent; color: var(--muted); min-height: 150px; }
.add-room span { font-size: 24px; }
.add-room strong { font-size: 11px; margin-top: 6px; color: var(--ink-2); }
.add-room small { font-size: 8.5px; }
.s-1 { background: #fdf9f3; }
.s-2 { opacity: .65; }
@media (max-width: 1100px) { .room-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 700px) { .room-grid { grid-template-columns: 1fr 1fr; } }
</style>
