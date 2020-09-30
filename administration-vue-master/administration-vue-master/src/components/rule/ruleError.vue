<template>
  <div>
    <Table :loading="loading" :columns="ruleErrorColumns" :data="ruleError" ref="table">
    </Table>
    <br>
    <Button type="info" @click="exportData(1)" >打印指定csv</Button>
    <br>
    <br>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="page"
      :page-sizes="[5,10,15]"
      :page-size="count"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </div>
</template>
<script>
export default {
  inject: ['reload'],
  data () {
    return {
      page: 1,
      count: 15,
      total: '',
      loading: true,
      ruleErrorColumns: [
        {
          title: '条数',
          key: 'msgId',
          align: 'center',
          width: 100,
        },
        {
          title: '项目',
          key: 'project',
          align: 'center',
          width: 100,
        },
        {
          title: '项目的机器',
          key: 'projectMachine',
          align: 'center',
        },
        {
          title: '机器的规则',
          key: 'rule',
          align: 'center',
        },
        {
          title: '机器数字表达式',
          key: 'ruleNumber',
          align: 'center',
        },
        {
          title: '报警的信息',
          key: 'msg',
          align: 'center',
        },
        {
          title: 'code',
          key: 'msgCode',
          align: 'center',
          width: 100,
        },
        {
          title: '机器报警时间',
          key: 'msgTime',
          align: 'center',
        }
      ],
      ruleError: [{
        msgId: '',
        project: '',
        projectMachine: '',
        rule: '',
        ruleNumber: '',
        msg: '',
        msgCode: '',
        msgTime: '',
      }],
    }
  },
  created() {
    //初始化规则且分页
    this.getErrorAll();
  },
  methods: {
    getErrorAll() {
      this.$http.post('/vue/getErrorAll', {
        page: this.page,
        count: this.count,
      }).then(res => {
        console.log(res);
        if (res.data.success === true) {
          this.ruleError = res.data.list;
          this.total = res.data.total;
          this.loading = false;
        } else {
          alert('微服务出了小状况,请再次刷新!');
        }
      })
    },
    // 初始页currentPage、初始每页数据数pagesize和数据data
    handleSizeChange: function (size) {
      this.count = size;
      console.log(this.count); //每页下拉显示数据
      this.getErrorAll();
    },
    handleCurrentChange: function (currentPage) {
      this.page = currentPage;
      console.log(this.page);  //点击第几页
      this.getErrorAll();
    },
    exportData (type) {
      if (type === 1) {
        this.$refs.table.exportCsv({
          filename: '机器消息失败CSV',
          quoted: true,
        });
      } else if (type === 2) {
        this.$http.post('/vue/orderMonthAllFeign',{
          monthStart: this.monthStart.toString(),
        }).then(res =>{
          console.log(res);
          this.$refs.table.exportCsv({
            filename: '支付宝月份账单',
            columns: this.orderColumns,
            data: res.data.list,
          });
        })
      } else if (type === 3) {
        this.$http.post('/vue/orderAllFeign')
          .then(res =>{
            console.log(res);
            this.$refs.table.exportCsv({
              filename: '支付宝用户全部的账单',
              columns: this.orderColumns,
              data: res.data.list,
            });
          })
      }
    }
  }
}
</script>

