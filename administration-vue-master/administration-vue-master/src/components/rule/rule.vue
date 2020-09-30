<template>
  <div>
    <Table :loading="loading" :columns="ruleColumns" :data="rule" ref="table">
    </Table>
    <br>
    <Button type="success" size="large" @click="insertRule=true">插入规则</Button>
    <Modal v-model="insertRule" draggable fullscreen scrollable :styles="{top: '20px'}"
            :footer-hide="true" title="插入组合规则(一次插入一整个组合，单个规则也算一个组合)">
      <Form ref="formDynamic" :model="formDynamic" :label-colon="true" :label-width="100" style="width: 100%">
        <FormItem
          label="标题(必填)">
          <label>
            <Input type="text" v-model="ruleTitle" maxlength="20"  show-word-limit clearable placeholder="输入你的组合规则标题"/>
          </label>
        </FormItem>
        <FormItem label="机器(必填)">
          <label>
            <Input type="textarea" v-model="machineList" maxlength="255" show-word-limit :rows="4"
                   clearable placeholder="输入你的机器,多的机器则以','分隔,例如：A,B,C,(最后的逗号不可以省略)" ></Input>
          </label>
        </FormItem>
        <FormItem
          v-for="(item, index) in formDynamic.items"
          v-if="item.status"
          :key="index"
          :label="'参数规则 ' + item.index"
          :prop="'items.' + index + '.leftExpression'"
          :rules="{required: true, message: '规则 ' + item.index +'不可以为空', trigger: 'blur'}">
          <Row>
            <Col span="18">
              <label>
                <Input type="text" v-model="item.leftExpression"  clearable show-word-limit maxlength="100" placeholder="输入左表达式"/>
                <Input type="text" v-model="item.midExpression"  clearable show-word-limit maxlength="10" placeholder="输入比较符号"/>
                <Input type="text" v-model="item.rightExpression"  clearable show-word-limit maxlength="100" placeholder="输入右表达式"/>
              </label>
            </Col>
            <Col span="4" offset="1">
              <Button type="error"  @click="handleRemove(index)" >删除</Button>
            </Col>
          </Row>
        </FormItem>
        <FormItem
          label="说明(必填)">
        <label>
            <Input v-model="note" type="textarea" maxlength="255" show-word-limit :rows="4"  placeholder="添加你的注释说明" />
          </label>
        </FormItem>
        <FormItem>
          <Row>
            <Col span="12">
              <Button type="success" size="small" long @click="handleAdd" icon="md-add">增加单条规则</Button>
            </Col>
          </Row>
        </FormItem>
        <Divider/>
        <FormItem>
          <Button type="primary" @click="handleSubmit('formDynamic')" v-preventReClick >提交保存</Button>
          <Button @click="handleReset('formDynamic')" style="margin-left: 8px" >重置规则</Button>
        </FormItem>
      </Form>
    </Modal>
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
      machineList: '',
      note: '',
      ruleTitle: '',
      index: 1,
      formDynamic: {
        items: [
          {
            leftExpression: '',
            midExpression: '',
            rightExpression: '',
            index: 1,
            status: 1
          }
        ]
      },
      insertRule: false,
      page: 1,
      count: 15,
      total: '',
      loading: true,
      ruleColumns: [
        {
          title: '规则',
          key: 'id',
          align: 'center',
          width: 100,
        },
        {
          title: '规则的标题',
          key: 'title',
          align: 'center',
        },
        {
          title: '规则左表达式',
          key: 'leftexpression',
          align: 'center',
        },
        {
          title: '规则中表达式',
          key: 'midexpression',
          align: 'center',
        },
        {
          title: '规则右表达式',
          key: 'rightexpression',
          align: 'center',
        },
        {
          title: '规则的注释',
          key: 'note',
          align: 'center',
        },
        {
          title: '规则的创建时间',
          key: 'time',
          align: 'center',
        }
      ],
      rule: [{
        id: '',
        title: '',
        leftexpression: '',
        midexpression: '',
        rightexpression: '',
        note: '',
        time: '',
      }],
    }
  },
  created() {
    //初始化规则且分页
    this.getRulesAll();
  },
  methods: {
    getRulesAll() {
      this.$http.post('/vue/getRulesAll', {
        page: this.page,
        count: this.count,
      }).then(res => {
        console.log(res);
        if (res.data.success === true) {
          this.rule = res.data.list;
          this.total = res.data.total;
          this.loading = false;
        } else {
          alert('微服务出了小状况,请再次刷新!');
        }
      })
    },
    //初始页currentPage、初始每页数据数pagesize和数据data
    handleSizeChange: function (size) {
      this.count = size;
      console.log(this.count); //每页下拉显示数据
      this.getRulesAll();
    },
    handleCurrentChange: function (currentPage) {
      this.page = currentPage;
      console.log(this.page);  //点击第几页
      this.getRulesAll();
    },
    handleSubmit (name) {
      let va=[];
      for (let i=0;i<this.formDynamic.items.length;i++){
        if (this.formDynamic.items[i].status!==0) {
          va.push(this.formDynamic.items[i].leftExpression + '-' + this.formDynamic.items[i].midExpression + "-" + this.formDynamic.items[i].rightExpression);
        }
      }
      this.$refs[name].validate((valid) => {
        if (valid && this.ruleTitle!=='' && this.note!=='' && this.machineList!=='') {
          this.$http.post('/vue/insertRules',{
            ruleTitle: this.ruleTitle,
            va: va.toString(),
            machineList: this.machineList,
            note: this.note,
          }).then(res =>{
            console.log(res);
            if (res.data.message===true) {
              this.$Message.success('插入组合规则成功');
              this.reload();
            }
          })
        } else {
          this.$Message.error('消息不可以为空');
        }
      })
    },
    handleReset (name) {
      this.$refs[name].resetFields();
    },
    handleAdd () {
      if (this.index>4){
        this.$message.error("组合规则达到上线")
      }else {
        this.index++;
        this.formDynamic.items.push({
          index: this.index,
          status: 1
        });
      }
    },
    handleRemove (index) {
      if (index===0){
        this.$message.error("至少有一条规则")
      }else {
        this.formDynamic.items[index].status = 0;
        this.index--;
      }
    },
  }
}
</script>

