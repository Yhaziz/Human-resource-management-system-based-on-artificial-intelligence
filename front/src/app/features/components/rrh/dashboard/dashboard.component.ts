import { DgService } from './../../../../core/services/dg.service';
import { Component, AfterViewInit, ElementRef } from '@angular/core';

import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { HlmSeparatorDirective } from '@spartan-ng/ui-separator-helm';
import ApexCharts from 'apexcharts';


import {
  HlmCardContentDirective,
  HlmCardDescriptionDirective,
  HlmCardDirective,
  HlmCardFooterDirective,
  HlmCardHeaderDirective,
  HlmCardTitleDirective,
} from '@spartan-ng/ui-card-helm';
import { DemandesData, DepartmentsUsers, User } from '../../../../core/types';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HlmSeparatorDirective,
    HlmCardContentDirective,
    HlmCardDescriptionDirective,
    HlmCardDirective,
    HlmCardFooterDirective,
    HlmCardHeaderDirective,
    HlmCardTitleDirective,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements AfterViewInit {
  users: User[];
  departments: DepartmentsUsers[];
  demandes: DemandesData;

  barAccepted: any = { name: "Accéptés", color: "#004c95", data: [], };
  barRejected: any = { name: "Refusés", color: "#a6b0bf", data: [], };
  barDepartmentName: string[];

  donutDataArray: number[];

  constructor(private dgService: DgService, private el: ElementRef) {
    this.users = this.dgService.dataUsers();
    this.departments = this.dgService.dataDepartmentsUsers();
    this.demandes = this.dgService.dataDemandes();
  }
  ngOnInit() {
    this.barData();
    this.donutData();

  }



  ngAfterViewInit() {



    const barOptions = {
      series: [this.barAccepted, this.barRejected],
      chart: {
        sparkline: {
          enabled: false,
        },
        type: "bar",
        width: "100%",
        height: 270,
        toolbar: {
          show: false,
        }
      },
      fill: {
        opacity: 1,
      },
      plotOptions: {
        bar: {
          horizontal: true,
          columnWidth: "100%",
          borderRadiusApplication: "end",
          borderRadius: 3,
          dataLabels: {
            position: "top",
          },
        },
      },
      legend: {
        show: false,
        position: "bottom",
      },
      dataLabels: {
        enabled: false,
      },
      tooltip: {
        shared: true,
        intersect: false,
        formatter: function (value) {
          return "" + value
        }
      },
      xaxis: {
        labels: {
          show: false,
          style: {
            fontFamily: "Inter, sans-serif",
            cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400'
          },
          formatter: function (value) {
            return "" + value
          }
        },
        categories: this.barDepartmentName,
        axisTicks: {
          show: false,
        },
        axisBorder: {
          show: false,
        },
      },
      yaxis: {
        labels: {
          show: true,
          style: {
            fontFamily: "Inter, sans-serif",
            cssClass: 'text-xs font-normal fill-gray-500 dark:fill-gray-400'
          }
        }
      },
      grid: {
        show: true,
        strokeDashArray: 4,
        padding: {
          left: 2,
          right: 2,
          top: -20
        },
      }
    }
    const barChartElement = this.el.nativeElement.querySelector('#bar-chart');
    if (barChartElement) {
      const barchart = new ApexCharts(barChartElement, barOptions);
      barchart.render();
    }



    const donutOptions = {
      series: this.donutDataArray,
      colors: ["#206bc4", "#a6b0bf", "#1E293B"],
      chart: {
        height: 220,
        width: "100%",
        type: "donut",
      },
      stroke: {
        colors: ["transparent"],
        lineCap: "",
      },
      plotOptions: {
        pie: {
          donut: {
            labels: {
              show: true,
              name: {
                show: true,
                fontFamily: "Inter, sans-serif",
                offsetY: 20,
              },
              total: {
                showAlways: true,
                show: true,
                label: "Demandes",
                fontFamily: "Inter, sans-serif",
                formatter: function (w) {
                  const sum = w.globals.seriesTotals.reduce((a, b) => {
                    return a + b
                  }, 0)
                  return '' + sum + ''
                },
              },
              value: {
                show: true,
                fontFamily: "Inter, sans-serif",
                offsetY: -20,
                formatter: function (value) {
                  return value + ""
                },
              },
            },
            size: "80%",
          },
        },
      },
      grid: {
        padding: {
          top: -2,
        },
      },
      labels: ["Congés", "A.Sortie", "E.Retard"],
      dataLabels: {
        enabled: false,
      },
      legend: {
        position: "bottom",
        fontFamily: "Inter, sans-serif",
      },
      yaxis: {
        labels: {
          formatter: function (value) {
            return value + ""
          },
        },
      },
      xaxis: {
        labels: {
          formatter: function (value) {
            return value + ""
          },
        },
        axisTicks: {
          show: false,
        },
        axisBorder: {
          show: false,
        },
      },
    }
    const donutChartElement = this.el.nativeElement.querySelector('#donut-chart');
    if (barChartElement) {
      const donutchart = new ApexCharts(donutChartElement, donutOptions);
      donutchart.render();
    }



  }

  barData(){
    if (!this.barDepartmentName) {
      this.barDepartmentName = [];
    }
    if (!this.barAccepted.data) {
      this.barAccepted.data = [];
    }
    if (!this.barRejected.data) {
      this.barRejected.data = [];
    }

    for (const department of this.departments) {

      let acceptedCount = 0;
      let rejectedCount = 0;

      for (const demand of this.demandes.decided.accepted) {
        if (department.users.find(user => user.id === demand.userId)) {
          acceptedCount++;
        }
      }

      for (const demand of this.demandes.decided.rejected) {
        if (department.users.find(user => user.id === demand.userId)) {
          rejectedCount++;
        }
      }

      if (!(acceptedCount===0 && rejectedCount===0)) {
        this.barDepartmentName.push(department.name);
        this.barAccepted.data.push(acceptedCount.toString());
        this.barRejected.data.push(rejectedCount.toString());
      }

    }
  }

  donutData(){
    if (!this.donutDataArray) {
      this.donutDataArray = [];
    }

    let congeCount = 0;
    let sortieCount = 0;
    let retardCount = 0;

    for (const demand of this.demandes.decided.all) {
      switch (demand.type) {
        case "CONGE":
          congeCount++;
          break;
        case "SORTIE":
          sortieCount++;
          break;
        case "RETARD":
          retardCount++;
          break;
        default:
          break;
      }
    }

    this.donutDataArray.push(congeCount, sortieCount, retardCount);
  }
}
