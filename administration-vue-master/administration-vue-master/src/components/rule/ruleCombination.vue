<template>
  <div>
    <Row>
      <Col v-for="(item,index1) in ruleComAll" :key="item.id">
      <Card style="background-color: azure;">
        <div style="text-align:center">
          <Form>
            <FormItem>
              组合标题：{{ titleList[index1]}}
              机器组合：{{ ruleComAll[index1].alertMachines}}
            </FormItem>
            <FormItem>
                <span style="color: red">
                  机器表达式：{{ realStringList[index1] }}
                 <br>
                </span>
            </FormItem>
            <FormItem>
              组合规则的注释: {{ruleComAll[index1].alertRuleNote}}<br>
              组合规则创建时间：{{ ruleComAll[index1].alertRuleTime }}
            </FormItem>
            <FormItem>
              <Button type="primary" @click="updateRule=true" v-on:click="setIndex(index1)" ghost >查看更新</Button>
              &nbsp;&nbsp;
              <Button type="error" @click="removeRule(index1)" ghost >删除规则</Button>
            </FormItem>
          </Form>
        </div>
    </Card>
      </Col>
    </Row>
    <Modal v-model="updateRule" draggable  fullscreen scrollable :styles="{top: '20px'}" :footer-hide="true" title="更新组合规则">
      <Form ref="formDynamic" :model="formDynamic" :label-colon="true" :label-width="100" style="width: 100%">
        <FormItem
          label="标题(必填)">
          <label>
            <Input type="text" v-model="ruleTitle" maxlength="20"  show-word-limit clearable  placeholder="输入你的组合规则标题"/>
          </label>
        </FormItem>
        <FormItem label="机器(必填)">
          <label>
            <Input type="textarea" v-model="machineList" maxlength="255" show-word-limit :rows="4"
                   clearable placeholder="输入你的机器,多的机器则以','分隔,例如：A,B,C,(最后的逗号不可以省略)" >
            </Input>
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
              <Button type="error" ghost @click="handleRemove(index)" >删除</Button>
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
              <Button type="success" size="small" long @click="handleAdd" icon="md-add" v-preventReClick>增加单条规则</Button>
            </Col>
          </Row>
        </FormItem>
        <Divider/>
        <FormItem>
          <Button type="primary" @click="handleSubmit('formDynamic')" v-preventReClick>提交更新</Button>
        </FormItem>
      </Form>
    </Modal>
    <br>
    <br>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[100,200,300]"
        :page-size="count"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
  </div>
</template>
<script>
export default {
  inject: ['reload'],
  data(){
    return {
      //动态变量index
      modelIndex: '',
      titleList: [],
      machineList: '',
      note: '',
      ruleTitle: '',
      index: 1,
      formDynamic: {
        items: [
          {
            comId: '',
            leftExpression: '',
            midExpression: '',
            rightExpression: '',
            index: 1,
            status: 1
          }
        ]
      },
      updateRule: false,
      page: 1,
      count: 100,
      total: '',
      ruleComAll: [{
        id: '',
        alertRuleId: '',
        alertMachines: '',
        alertRuleNote: '',
        alertRuleTime: '',
      }],
      rule: [{
        id: '',
        title: '',
        leftexpression: '',
        midexpression: '',
        rightexpression: '',
        note: '',
        time: '',
      }],
      stringList: [],
      realStringList: [],
    }
  },
  created() {
    this.getRuleCombination();
  },
  methods: {
    // 初始页currentPage、初始每页数据数pagesize和数据data
    handleSizeChange: function (size) {
      this.count = size;
      console.log(this.count); //每页下拉显示数据
      this.getRuleCombination();
    },
    handleCurrentChange: function (currentPage) {
      this.page = currentPage;
      console.log(this.page);  //点击第几页
      this.getRuleCombination();
    },
    getRuleCombination(){
      this.$http.post('/vue/getRuleCombination', {
        page: this.page,
        count: this.count,
      }).then(res => {
        console.log(res);
        if (res.data.success === true) {
          this.ruleComAll = res.data.ruleCombinationList;
          this.rule=res.data.ruleList;
          this.total = res.data.total;
          this.titleList=res.data.titleList;
          this.stringList=res.data.stringList;
          this.realStringList=res.data.realList;
        }else {
          alert('微服务出了小状况,请再次刷新!');
        }
      })
    },
    handleAdd () {
      if (this.index>5){
        this.$message.error("组合规则达到上线")
      }else {
        this.formDynamic.items.push({
          id: '',
          index: this.index++,
          status: 1
        });
      }
    },
    handleSubmit (name) {
      //控制每一项的规则
      let va=[];
      for (let i=0;i<this.formDynamic.items.length;i++){
        if (this.formDynamic.items[i].leftExpression!=='' && this.formDynamic.items[i].midExpression!==''
          && this.formDynamic.items[i].rightExpression!=='') {
          va.push(this.formDynamic.items[i].leftExpression + '-' +
            this.formDynamic.items[i].midExpression + "-" + this.formDynamic.items[i].rightExpression);
        }else {
          this.$message.error("规则表达式不正确");
          return;
        }
      }

      //控制每一项的规则Id
      let ruleIdList="";
      for (let j=0;j<this.formDynamic.items.length;j++){
        if (this.formDynamic.items[j].comId!=='' && this.formDynamic.items[j].comId!==null
        && this.formDynamic.items[j].comId!==undefined){
          ruleIdList=ruleIdList+this.formDynamic.items[j].comId+",";
          console.log(ruleIdList+'-');
        }
      }

      this.$refs[name].validate((valid) => {
        if (valid && this.ruleTitle!=='' && this.note!=='' && this.machineList!=='') {
          this.$http.post('/vue/updateRules',{
            id: this.ruleComAll[this.modelIndex].id,
            comId: ruleIdList.toString(),
            ruleTitle: this.ruleTitle,
            va: va.toString(),
            machineList: this.machineList,
            note: this.note,
          }).then(res =>{
            console.log(res);
            if (res.data.message===true) {
              this.$Message.success('更新组合规则成功');
              this.reload();
            }
          })
        } else {
          this.$Message.error('消息不可以为空');
        }
      })
    },
    handleRemove (index) {
      if (index===0){
        this.$message.error("至少有一条规则")
      }else {
        this.formDynamic.items[index].status = 0;
        this.formDynamic.items.splice(index,1);
        this.index--;
      }
    },
    setIndex(index0) {
      this.modelIndex=index0;
      while (this.formDynamic.items.length > 0) {
        this.formDynamic.items.pop();
        this.index=1;
      }
      //分割主键Id
      let abc=this.ruleComAll[this.modelIndex].alertRuleId.split(",");
      this.ruleTitle = this.titleList[index0];
      this.machineList = this.ruleComAll[index0].alertMachines;
      this.note = this.ruleComAll[index0].alertRuleNote;
      let c=this.stringList[index0].split('-');
      for (let i=0;i<c.length;i++){
        if (c[i]!=='') {
          let b = c[i].split(',');
          this.formDynamic.items.push({
            comId: abc[i],
            leftExpression: b[0],
            midExpression: b[1],
            rightExpression: b[2],
            index: this.index++,
            status: 1
          })
        }
      }
    },
    //删除整个规则
    removeRule(index2){
      this.$Modal.confirm({
        title: '删除：'+this.titleList[index2],
        content: '<p>删除组合规则后不可恢复</p>',
        onOk: () => {
          this.$http.post('/vue/deleteRules',{
            id: this.ruleComAll[index2].id,
          }).then(res =>{
            console.log(res);
            if (res.data.message===true) {
              this.$Message.success('成功删除组合规则');
              this.reload();
            }
          })
        },
        onCancel: () => {
          this.$Message.info('已取消删除');
        }
      });
    }
  }
}
</script>
<style>
footer{
  width: 100%;
  position: absolute;
  bottom: 0
}
</style>
