# -*- coding: utf-8 -*-
{
    'name': 'Dayflow HRMS',
    'version': '17.0.1.0.0',
    'category': 'Human Resources',
    'summary': 'Dayflow Human Resource Management System',
    'description': """
Dayflow HRMS - Core Module
===========================
Modular HRMS foundation for Dayflow project providing:
- Security groups (Employee, HR / Administrator)
- Core menu navigation structure
- Scalable foundation for modular feature development
    """,
    'author': 'Dayflow Team',
    'website': 'https://github.com/ramya-r-25/Odoo_demo',
    'license': 'LGPL-3',
    'depends': ['base'],
    'data': [
        'security/dayflow_security.xml',
        'security/ir.model.access.csv',
        'views/dayflow_menus.xml',
    ],
    'installable': True,
    'application': True,
    'auto_install': False,
}
