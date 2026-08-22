# -*- coding: utf-8 -*-

from odoo import models, fields

class DayflowEmployee(models.Model):
    _name = 'dayflow.employee'
    _description = 'Dayflow Employee Profile'

    employee_id = fields.Char(string='Employee ID', required=True, copy=False)
    name = fields.Char(string='Name', required=True)
    email = fields.Char(string='Email')
    phone = fields.Char(string='Phone')
    address = fields.Text(string='Address')
    job_position = fields.Char(string='Job Position')
    department = fields.Char(string='Department')
    salary_structure = fields.Selection([
        ('full_time', 'Full-time / Fixed Salary'),
        ('part_time', 'Part-time'),
        ('hourly', 'Hourly Rate'),
        ('contract', 'Contractual')
    ], string='Salary Structure', default='full_time')
    image = fields.Binary(string='Profile Picture', attachment=True)
    document_ids = fields.Many2many('ir.attachment', string='Documents')
